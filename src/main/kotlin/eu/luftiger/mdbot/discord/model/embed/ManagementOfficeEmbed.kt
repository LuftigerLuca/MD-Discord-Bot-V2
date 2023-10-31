package eu.luftiger.mdbot.discord.model.embed

import eu.luftiger.mdbot.discord.DiscordBot
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import java.awt.Color

class ManagementOfficeEmbed(private val discordBot: DiscordBot) : ListenerAdapter() {

    fun send(messageChannel: MessageChannel) {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setTitle("Medical Department Los Santos - Leitungs-Sekretariat")
        embedBuilder.setDescription("**Sehr geehrter Mitarbeiter**,\n herzlich willkommen im Leitungs-Sekretariat des Medical Departments! \n\n Bitte wählen Sie einen Themenreiter aus.")

        embedBuilder.setColor(Color.red)
        embedBuilder.setThumbnail("https://cdn.discordapp.com/attachments/1138170607377317984/1144596659460919316/16832354958303407225275064761398.png")

        val hireButton = Button.secondary("hire", "Einstellen").withEmoji(Emoji.fromUnicode("U+1F4DD")).asDisabled()
        val fireButton = Button.secondary("fire", "Entlassen").withEmoji(Emoji.fromUnicode("U+1F4DD")).asDisabled()
        val promoteButton = Button.primary("promote", "Befördern").withEmoji(Emoji.fromUnicode("U+2B06"))

        messageChannel.sendMessageEmbeds(embedBuilder.build()).addActionRow(
            hireButton,
            fireButton,
            promoteButton
        ).queue()
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        when (event.componentId) {
            "promote" -> discordBot.promoteModal.reply(event)
        }
    }
}