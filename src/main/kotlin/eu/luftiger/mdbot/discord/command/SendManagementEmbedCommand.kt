package eu.luftiger.mdbot.discord.command

import eu.luftiger.mdbot.discord.DiscordBot
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class SendManagementEmbedCommand : BaseCommand {

    override fun execute(discordBot: DiscordBot, event: SlashCommandInteractionEvent) {
        val configuration = discordBot.bot.configuration

        if (!discordBot.guildsProvider.hasPermission(event.guild!!.id, event.user.id, "send_office_embed")) {
            event.reply("Du hast keine Berechtigung diesen Befehl auszuführen!").setEphemeral(true).queue()
            return
        }

        discordBot.managementOfficeEmbed.send(event.messageChannel)
        event.reply("erfolgreich gesendet!").setEphemeral(true).queue()
    }
}