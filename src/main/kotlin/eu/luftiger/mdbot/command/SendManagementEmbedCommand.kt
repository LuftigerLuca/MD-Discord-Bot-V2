package eu.luftiger.mdbot.command

import eu.luftiger.mdbot.Bot
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class SendManagementEmbedCommand : BaseCommand {

    override fun execute(bot: Bot, event: SlashCommandInteractionEvent) {
        val configuration = bot.configuration

        if(!bot.guildsProvider.hasPermission(event.guild!!.id, event.user.id, "send_office_embed")){
            event.reply("Du hast keine Berechtigung diesen Befehl auszuführen!").setEphemeral(true).queue()
            return
        }

        bot.managementOfficeEmbed.send(event.messageChannel)
        event.reply("erfolgreich gesendet!").setEphemeral(true).queue()
    }
}