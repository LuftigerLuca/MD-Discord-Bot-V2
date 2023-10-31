package eu.luftiger.mdbot.discord.model.embed

import eu.luftiger.mdbot.discord.DiscordBot
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import java.awt.Color

class TrainingEmbed(private val discordBot: DiscordBot) : ListenerAdapter() {

    fun send(messageChannel: MessageChannel, name: String, date: String, description: String, location: String, requirements: String, maxParticipants: Int, author: String, authorAvatarUri: String) {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setColor(Color.yellow)
        embedBuilder.setTitle(name)
        embedBuilder.setAuthor(author, null, authorAvatarUri)
        embedBuilder.setDescription(description)
        embedBuilder.addField("Datum", date, false)
        embedBuilder.addField("Ort", location, false)
        embedBuilder.addField("Voraussetzungen", requirements, false)

        val participants = "\n-".repeat(0.coerceAtLeast(maxParticipants))
        embedBuilder.addField("Teilnehmer", "[0/$maxParticipants]$participants", false)

        messageChannel.sendMessageEmbeds(embedBuilder.build()).addActionRow(
            Button.success("training:join", "Teilnehmen").withEmoji(Emoji.fromUnicode("U+2705")),
            Button.danger("training:leave", "Absagen").withEmoji(Emoji.fromUnicode("U+274C")),
            Button.danger("training:delete", " ").withEmoji(Emoji.fromUnicode("U+1F5D1"))
        ).queue()
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        when (event.componentId) {
            "training:join" -> joinButton(event)
            "training:leave" -> leaveButton(event)
            "training:delete" -> deleteButton(event)
        }
    }

    private fun joinButton(event: ButtonInteractionEvent) {
        val messageEmbed = event.message.embeds[0]
        val participantsField = messageEmbed.fields[3]
        val participants = participantsField.value!!.split("\n-").toMutableList()
        participants.removeAt(0)
        participants.removeAll(listOf("", " "))

        if (participants.contains(event.user.asMention)) {
            event.reply("Du bist bereits als Teilnehmer eingetragen!").setEphemeral(true).queue()
            return
        }

        val maxParticipants = participantsField.value!!.split("/")[1].split("]")[0].toInt()
        if (participants.size - 1 >= maxParticipants) {
            event.reply("Die maximale Anzahl an Teilnehmern wurde bereits erreicht!").setEphemeral(true).queue()
            return
        }

        val newParticipants = participants.toMutableList()
        newParticipants.add(event.user.asMention)

        val newSize = newParticipants.size

        for (i in newParticipants.size..<maxParticipants - participants.size) {
            newParticipants.add("")
        }

        val isFull = newSize == maxParticipants


        val embedBuilder = EmbedBuilder()
        if (isFull) embedBuilder.setColor(Color.red) else embedBuilder.setColor(Color.yellow)
        embedBuilder.setTitle(messageEmbed.title)
        embedBuilder.setAuthor(messageEmbed.author!!.name, null, messageEmbed.author!!.iconUrl)
        embedBuilder.setDescription(messageEmbed.description)
        embedBuilder.addField("Datum", messageEmbed.fields[0].value!!, false)
        embedBuilder.addField("Ort", messageEmbed.fields[1].value!!, false)
        embedBuilder.addField("Voraussetzungen", messageEmbed.fields[2].value!!, false)
        embedBuilder.addField("Teilnehmer", "[${newSize}/$maxParticipants]\n-${newParticipants.joinToString("\n-")}", false)

        event.message.editMessageEmbeds(embedBuilder.build()).queue()
        event.reply("Du wurdest als Teilnehmer eingetragen!").setEphemeral(true).queue()
    }

    private fun leaveButton(event: ButtonInteractionEvent) {
        val messageEmbed = event.message.embeds[0]
        val participantsField = messageEmbed.fields[3]
        val participants = participantsField.value!!.split("\n-").toMutableList()
        participants.removeAt(0)
        participants.removeAll(listOf("", " "))

        if (!participants.contains(event.user.asMention)) {
            event.reply("Du bist nicht als Teilnehmer eingetragen!").setEphemeral(true).queue()
            return
        }

        val maxParticipants = participantsField.value!!.split("/")[1].split("]")[0].toInt()

        val newParticipants = participants.toMutableList()
        newParticipants.remove(event.user.asMention)

        val newSize = newParticipants.size

        for (i in newParticipants.size..<maxParticipants - participants.size) {
            newParticipants.add("")
        }

        val embedBuilder = EmbedBuilder()
        embedBuilder.setColor(Color.yellow)
        embedBuilder.setTitle(messageEmbed.title)
        embedBuilder.setAuthor(messageEmbed.author!!.name, null, messageEmbed.author!!.iconUrl)
        embedBuilder.setDescription(messageEmbed.description)
        embedBuilder.addField("Datum", messageEmbed.fields[0].value!!, false)
        embedBuilder.addField("Ort", messageEmbed.fields[1].value!!, false)
        embedBuilder.addField("Voraussetzungen", messageEmbed.fields[2].value!!, false)
        embedBuilder.addField("Teilnehmer", "[${newSize}/$maxParticipants]\n-${newParticipants.joinToString("\n-")}", false)

        event.message.editMessageEmbeds(embedBuilder.build()).queue()
        event.reply("Du wurdest als Teilnehmer ausgetragen!").setEphemeral(true).queue()
    }

    private fun deleteButton(event: ButtonInteractionEvent) {
        if (!discordBot.guildsProvider.hasPermission(event.guild!!.id, event.user.id, "training_delete")) {
            event.reply("Du hast keine Berechtigung, um diese Abmeldung zu löschen!").setEphemeral(true).queue()
            return
        }

        event.message.delete().queue()
        event.reply("Die Nachricht wurde gelöscht!").setEphemeral(true).queue()
    }

}