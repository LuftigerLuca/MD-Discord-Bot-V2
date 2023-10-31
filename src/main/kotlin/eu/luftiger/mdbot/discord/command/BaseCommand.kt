package eu.luftiger.mdbot.discord.command

import eu.luftiger.mdbot.discord.DiscordBot
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

interface BaseCommand {
    fun execute(discordBot: DiscordBot, event: SlashCommandInteractionEvent)
}