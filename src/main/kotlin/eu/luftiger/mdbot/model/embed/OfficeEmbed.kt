package eu.luftiger.mdbot.model.embed

import eu.luftiger.mdbot.Bot
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import java.awt.Color

class OfficeEmbed(val bot: Bot) : ListenerAdapter() {

    fun send(messageChannel: MessageChannel){
        val embedBuilder = EmbedBuilder()
        embedBuilder.setTitle("Medical Department Los Santos - Sekretariat")
        embedBuilder.setDescription("**Sehr geehrter Mitarbeiter**,\n herzlich willkommen im Sekretariat des Medical Departments! \n\n Bitte wählen Sie einen Themenreiter aus.")

        embedBuilder.addField("Leitstellenblatt", "[hier klicken](https://discord.com/channels/1138170603967356958/1138170605754122427)", true)
        embedBuilder.addField("Dienstvorschriften", "[hier klicken](https://discord.com/channels/1138170603967356958/1138170605754122426)", true)
        embedBuilder.addField("Kleiderordnung", "[hier klicken](https://discord.com/channels/1138170603967356958/1138170605754122428)", true)
        embedBuilder.setColor(Color.red)
        embedBuilder.setThumbnail("https://cdn.discordapp.com/attachments/1138170607377317984/1144596659460919316/16832354958303407225275064761398.png")

        val stampInButton = Button.secondary("stampin", "einstempeln").asDisabled()
        val stampOutButton = Button.secondary("stampout", "ausstempeln").asDisabled()

        val signoffButton = Button.primary("signoff", "Abmeldung beantragen").withEmoji(Emoji.fromUnicode("U+1F4C3"))
        val sicknoteButton = Button.primary("sicknote", "Krankschreibung erstellen").withEmoji(Emoji.fromUnicode("U+1F691"))

        messageChannel.sendMessageEmbeds(embedBuilder.build()).addActionRow(
            stampInButton,
            stampOutButton,
            signoffButton,
            sicknoteButton
        ).queue()
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        when(event.componentId){
            "signoff" -> signOffButton(event)
            "sicknote" -> sickNoteButton(event)
        }
    }

    private fun signOffButton(event: ButtonInteractionEvent) {
        bot.signOffModal.reply(event)
    }

    private fun sickNoteButton(event: ButtonInteractionEvent) {
        bot.sickNoteModal.reply(event)
    }
}