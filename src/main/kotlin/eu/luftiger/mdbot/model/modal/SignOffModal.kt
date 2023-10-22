package eu.luftiger.mdbot.model.modal

import eu.luftiger.mdbot.Bot
import eu.luftiger.mdbot.util.isDate
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.InteractionType
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal
import java.text.SimpleDateFormat
import java.util.*

class SignOffModal(private val bot: Bot) : ListenerAdapter() {

    fun reply(event: GenericComponentInteractionCreateEvent){
        if(!bot.guildsProvider.hasPermission(event.guild!!.id, event.user.id, "signoff_create")){
            event.reply("Du hast keine Berechtigung, um eine Abmeldung zu erstellen!").setEphemeral(true).queue()
            return
        }

        val reasonInput = TextInput.create("reason", "Grund für die Abmeldung", TextInputStyle.SHORT)
            .setRequired(true)
            .setPlaceholder("z.B. Urlaub")
            .build()

        val fromDateInput = TextInput.create("fromdate", "Start der Abmeldung", TextInputStyle.SHORT)
            .setRequired(true)
            .setValue(SimpleDateFormat("dd.MM.yyyy").format(Date()))
            .setPlaceholder("Format: dd.mm.yyyy, z.B. 01.01.2021")
            .build()

        val toDateInput = TextInput.create("todate", "Ende der Abmeldung", TextInputStyle.SHORT)
            .setRequired(false)
            .setPlaceholder("Format: dd.mm.yyyy, z.B. 01.01.2021")
            .build()

        val modal = Modal.create("signoff", "Abmeldung beantragen")
            .addActionRows(ActionRow.of(reasonInput), ActionRow.of(fromDateInput), ActionRow.of(toDateInput))

        event.replyModal(modal.build()).queue()
    }

    override fun onModalInteraction(event: ModalInteractionEvent) {
        if(event.modalId != "signoff") return
        if(event.type != InteractionType.MODAL_SUBMIT) return

        val guild = event.guild

        var todate: String? = event.getValue("todate")?.asString
        if (todate == null) {
            todate = event.getValue("fromdate")?.asString
        }

        if(!event.getValue("fromdate")!!.asString.isDate() || !todate!!.isDate()){
            event.reply("Das angegebene Datum ist ungültig!").setEphemeral(true).queue()
            return
        }

        if(event.guild == null){
            event.reply("Dieser Befehl kann nur auf einem Server ausgeführt werden!").setEphemeral(true).queue()
            return
        }

        val botGuild = bot.guildsProvider.getGuild(event.guild!!.id)
        if(botGuild == null){
            event.reply("Dieser Server ist nicht registriert!").setEphemeral(true).queue()
            return
        }

        val signOffChannel = botGuild.guildSettings!!.signOffChannelId
        if(signOffChannel == null){
            event.reply("Es wurde kein Abmelde-Kanal festgelegt!").setEphemeral(true).queue()
            return
        }

        val textChannel = event.guild!!.getTextChannelById(signOffChannel)
        if(textChannel == null){
            event.reply("Der Abmelde-Kanal konnte nicht gefunden werden!").setEphemeral(true).queue()
            return
        }

        val author = event.member!!.nickname ?: event.user.name

        bot.signOffEmbed.send(textChannel,
            event.getValue("fromdate")!!.asString,
            event.getValue("todate")!!.asString,
            event.getValue("reason")!!.asString,
            author,
            event.user.avatarUrl!!)

        event.reply("Die Abmeldung wurde eingereicht!").setEphemeral(true).queue()
    }
}