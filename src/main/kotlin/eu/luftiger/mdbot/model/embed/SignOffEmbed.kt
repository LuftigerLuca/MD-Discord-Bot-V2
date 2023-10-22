package eu.luftiger.mdbot.model.embed

import eu.luftiger.mdbot.Bot
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import java.awt.Color

class SignOffEmbed(private val bot: Bot) : ListenerAdapter(){

    fun send(messageChannel: MessageChannel, fromdate: String, todate: String, reason: String, author: String, authorAvatar: String){
        val embedBuilder = EmbedBuilder()
        embedBuilder.setTitle("Abmeldung")
        embedBuilder.addField("Grund", reason, false)
        embedBuilder.addField("Von", fromdate, true)
        embedBuilder.addField("Bis", todate, true)
        embedBuilder.addField("Status", "Offen", false)
        embedBuilder.setAuthor(author, null, authorAvatar)
        embedBuilder.setColor(Color.yellow)

        val deleteButton = Button.danger("signoff:delete", " ").withEmoji(Emoji.fromUnicode("U+1F5D1"))
        val acceptButton = Button.success("signoff:accept", "Akzeptieren")
        val declineButton = Button.danger("signoff:decline", "Ablehnen")

        messageChannel.sendMessageEmbeds(embedBuilder.build())
            .addActionRow(deleteButton, acceptButton, declineButton)
            .queue()
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        when(event.componentId){
            "signoff:delete" -> deleteButton(event)
            "signoff:accept" -> acceptButton(event)
            "signoff:decline" -> declineButton(event)
        }
    }

    private fun deleteButton(event: ButtonInteractionEvent) {
        if(!bot.guildsProvider.hasPermission(event.guild!!.id, event.user.id, "signoff_delete")){
            event.reply("Du hast keine Berechtigung, um diese Abmeldung zu löschen!").setEphemeral(true).queue()
            return
        }

        event.message.delete().queue()
        event.reply("Die Krankmeldung wurde gelöscht!").setEphemeral(true).queue()
    }

    private fun acceptButton(event: ButtonInteractionEvent) {
        if(!bot.guildsProvider.hasPermission(event.guild!!.id, event.user.id, "signoff_accept")){
            event.reply("Du hast keine Berechtigung, um diese Abmeldung zu akzeptieren!").setEphemeral(true).queue()
            return
        }

        val messageEmbed = event.message.embeds[0]
        val embedBuilder = EmbedBuilder()
        val acceptedBy = event.member!!.nickname ?: event.user.name

        embedBuilder.setTitle("Abmeldung")
        embedBuilder.addField("Grund", messageEmbed.fields[0].value!!, false)
        embedBuilder.addField("Von", messageEmbed.fields[1].value!!, true)
        embedBuilder.addField("Bis", messageEmbed.fields[2].value!!, true)
        embedBuilder.addField("Status", "Angenommen durch $acceptedBy", false)
        embedBuilder.setAuthor(messageEmbed.author!!.name, null, messageEmbed.author!!.iconUrl)
        embedBuilder.setColor(Color.GREEN)

        val deleteButton = Button.danger("signoff:delete", " ").withEmoji(Emoji.fromUnicode("U+1F5D1"))
        val acceptButton = Button.success("signoff:accept", "Akzeptieren").asDisabled()
        val declineButton = Button.danger("signoff:decline", "Ablehnen").asEnabled()

        event.message.editMessageEmbeds(embedBuilder.build())
            .setActionRow(deleteButton, acceptButton, declineButton)
            .queue()

        event.reply("Die Abmeldung wurde akzeptiert!").setEphemeral(true).queue()
    }

    private fun declineButton(event: ButtonInteractionEvent){
        if(!bot.guildsProvider.hasPermission(event.guild!!.id, event.user.id, "signoff_decline")){
            event.reply("Du hast keine Berechtigung, um diese Abmeldung abzulehnen!").setEphemeral(true).queue()
            return
        }

        val messageEmbed = event.message.embeds[0]
        val embedBuilder = EmbedBuilder()
        val declinedBy = event.member!!.nickname ?: event.user.name

        embedBuilder.setTitle("Abmeldung")
        embedBuilder.addField("Grund", messageEmbed.fields[0].value!!, false)
        embedBuilder.addField("Von", messageEmbed.fields[1].value!!, true)
        embedBuilder.addField("Bis", messageEmbed.fields[2].value!!, true)
        embedBuilder.addField("Status", "Abgelehnt durch $declinedBy", false)
        embedBuilder.setAuthor(messageEmbed.author!!.name, null, messageEmbed.author!!.iconUrl)
        embedBuilder.setColor(Color.RED)

        val deleteButton = Button.danger("signoff:delete", " ").withEmoji(Emoji.fromUnicode("U+1F5D1"))
        val acceptButton = Button.success("signoff:accept", "Akzeptieren").asEnabled()
        val declineButton = Button.danger("signoff:decline", "Ablehnen").asDisabled()

        event.message.editMessageEmbeds(embedBuilder.build())
            .setActionRow(deleteButton, acceptButton, declineButton)
            .queue()

        event.reply("Die Abmeldung wurde abgelehnt!").setEphemeral(true).queue()
    }
}