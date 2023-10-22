package eu.luftiger.mdbot.model.modal

import eu.luftiger.mdbot.Bot
import eu.luftiger.mdbot.util.isDate
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


class SickNoteModal(private val bot: Bot) : ListenerAdapter() {

    fun reply(event: GenericComponentInteractionCreateEvent){
        if(!bot.guildsProvider.hasPermission(event.guild!!.id, event.user.id, "sicknote_create")){
            event.reply("Du hast keine Berechtigung, um eine Krankschreibung zu erstellen!").setEphemeral(true).queue()
            return
        }

        val nameInput = TextInput.create("name", "Name oder Dienstnummer des Patienten", TextInputStyle.SHORT)
            .setRequired(true)
            .setPlaceholder("z.B. Max Mustermann oder PD-23")
            .build()

        val reasonInput = TextInput.create("reason", "Grund für die Krankschreibung", TextInputStyle.SHORT)
            .setRequired(true)
            .setPlaceholder("z.B. Fraktur des rechten Beins")
            .build()

        val fromDateInput = TextInput.create("fromdate", "Start der Krankschreibung", TextInputStyle.SHORT)
            .setRequired(true)
            .setValue(SimpleDateFormat("dd.MM.yyyy").format(Date()))
            .setPlaceholder("Format: dd.mm.yyyy, z.B. 01.01.2021")
            .build()

        val toDateInput = TextInput.create("todate", "Ende der Krankschreibung", TextInputStyle.SHORT)
            .setRequired(true)
            .setPlaceholder("Format: dd.mm.yyyy, z.B. 01.01.2021")
            .build()

        val additionalInput = TextInput.create("additional", "Zusätzliche Informationen", TextInputStyle.PARAGRAPH)
            .setRequired(false)
            .setPlaceholder("z.B. Büroarbeit möglich")
            .setMaxLength(100)
            .build()

        val modal = Modal.create("sicknote", "Krankmeldung erstellen")
            .addActionRows(
                ActionRow.of(nameInput),
                ActionRow.of(reasonInput),
                ActionRow.of(fromDateInput),
                ActionRow.of(toDateInput),
                ActionRow.of(additionalInput)
            )

        event.replyModal(modal.build()).queue()
    }

    override fun onModalInteraction(event: ModalInteractionEvent) {
        if(event.modalId != "sicknote") return
        if(event.type != InteractionType.MODAL_SUBMIT) return

        val guild = event.guild
        if(!event.getValue("fromdate")!!.asString.isDate() || !event.getValue("todate")!!.asString.isDate()){
            event.reply("Das angegebene Datum ist ungültig!").setEphemeral(true).queue()
            return
        }

        if(guild == null){
            event.reply("Dieser Befehl kann nur auf einem Server ausgeführt werden!").setEphemeral(true).queue()
            return
        }

        val botGuild = bot.guildsProvider.getGuild(guild.id)
        if(botGuild == null){
            event.reply("Dieser Server ist nicht registriert!").setEphemeral(true).queue()
            return
        }

        val sickNoteChannel = botGuild.guildSettings!!.sickNoteChannelId
        if(sickNoteChannel == null){
            event.reply("Es wurde kein Krankschreibungen-Kanal festgelegt!").setEphemeral(true).queue()
            return
        }

        val textChannel = guild.getTextChannelById(sickNoteChannel)
        if(textChannel == null){
            event.reply("Der Krankschreibungen-Kanal konnte nicht gefunden werden!").setEphemeral(true).queue()
            return
        }

        val author = event.member!!.nickname ?: event.user.name

        bot.sickNoteEmbed.send(textChannel,
            event.getValue("name")!!.asString,
            event.getValue("fromdate")!!.asString,
            event.getValue("todate")!!.asString,
            event.getValue("reason")!!.asString,
            event.getValue("additional")!!.asString,
            author, event.user.avatarUrl!!)

        event.reply("Die Krankschreibung wurde erstellt!").setEphemeral(true).queue()
    }
}