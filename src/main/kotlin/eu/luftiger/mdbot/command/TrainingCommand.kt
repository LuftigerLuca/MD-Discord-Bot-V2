package eu.luftiger.mdbot.command

import eu.luftiger.mdbot.Bot
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class TrainingCommand : BaseCommand{

    override fun execute(bot: Bot, event: SlashCommandInteractionEvent) {
        val configuration = bot.configuration

        if(!bot.guildsProvider.hasPermission(event.guild!!.id, event.user.id, "send_training_embed")){
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

        bot.trainingEmbed.send(event.messageChannel, name, date, description, location, requirements, maxParticipants, author, event.user.avatarUrl!!)
        event.reply("erfolgreich gesendet!").setEphemeral(true).queue()
    }
}