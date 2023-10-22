package eu.luftiger.mdbot.model.embed

import eu.luftiger.mdbot.Bot
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import java.awt.Color

class SickNoteEmbed(private val bot: Bot) : ListenerAdapter(){

    fun send(messageChannel: MessageChannel, name: String, fromdate: String, todate: String, reason: String, additional: String, author: String, authorAvatar: String){

        val embedBuilder = EmbedBuilder()
        embedBuilder.setTitle("Krankschreibung")
        embedBuilder.addField("Name", name, false)
        embedBuilder.addField("Von", fromdate, true)
        embedBuilder.addField("Bis", todate, true)
        embedBuilder.addField("Grund", reason, false)
        if(additional != "") embedBuilder.addField("Zusatz", additional, false)
        embedBuilder.setAuthor(author, null, authorAvatar)
        embedBuilder.setColor(Color.red)

        val deleteButton = Button.danger("sicknote:delete", " ").withEmoji(Emoji.fromUnicode("U+1F5D1"))

        messageChannel.sendMessageEmbeds(embedBuilder.build())
            .addActionRow(deleteButton)
            .queue()
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        if(event.componentId != "sicknote:delete") return
        if(!bot.guildsProvider.hasPermission(event.guild!!.id, event.user.id, "sicknote_delete")){
            event.reply("Du hast keine Berechtigung, um diese Krankmeldung zu löschen!").setEphemeral(true).queue()
            return
        }

        event.message.delete().queue()
        event.reply("Die Krankmeldung wurde gelöscht!").setEphemeral(true).queue()
    }
}