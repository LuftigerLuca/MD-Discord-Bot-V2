package eu.luftiger.mdbot.command

import eu.luftiger.mdbot.Bot
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

interface BaseCommand {
    fun execute(bot: Bot, event: SlashCommandInteractionEvent)
}