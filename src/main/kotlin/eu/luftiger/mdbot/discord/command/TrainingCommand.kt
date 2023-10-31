package eu.luftiger.mdbot.discord.command

import eu.luftiger.mdbot.discord.DiscordBot
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class TrainingCommand : BaseCommand {

    override fun execute(discordBot: DiscordBot, event: SlashCommandInteractionEvent) {
        val configuration = discordBot.bot.configuration

        if (!discordBot.guildsProvider.hasPermission(event.guild!!.id, event.user.id, "send_training_embed")) {
            event.reply("Du hast keine Berechtigung diesen Befehl auszuführen!").setEphemeral(true).queue()
            return
        }

        val name = event.getOption("name")!!.asString
        val date = event.getOption("date")!!.asString
        val description = event.getOption("description")!!.asString
        val location = event.getOption("location")!!.asString
        val requirements = event.getOption("requirements")!!.asString
        val maxParticipants = event.getOption("maxparticipants")!!.asInt
        val author = event.member!!.nickname ?: event.user.name

        discordBot.trainingEmbed.send(event.messageChannel, name, date, description, location, requirements, maxParticipants, author, event.user.avatarUrl!!)
        event.reply("erfolgreich gesendet!").setEphemeral(true).queue()
    }
}