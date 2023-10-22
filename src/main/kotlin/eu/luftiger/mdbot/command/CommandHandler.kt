package eu.luftiger.mdbot.command

import eu.luftiger.mdbot.Bot
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands

class CommandHandler(private val bot: Bot) : ListenerAdapter() {

    private val jda = bot.jda

    fun registerCommands(){
        val sendOfficeEmbedCommand = Commands.slash("sendofficeembed", "Sends the main embed with buttons in a channel.")
        val sendManagementEmbedCommand = Commands.slash("sendmanagementembed", "Sends the management embed with buttons in a channel.")
        val trainingCommand = Commands.slash("training", "Sends the training embed with buttons in a channel.")
            .addOption(OptionType.STRING, "name", "Name of the training", true)
            .addOption(OptionType.STRING, "date", "Date of the training, format: dd.MM.yyyy mm:hh", true)
            .addOption(OptionType.STRING, "description", "Description of the training", true)
            .addOption(OptionType.STRING, "location", "Location of the training", true)
            .addOption(OptionType.STRING, "requirements", "Requirements for the training", true)
            .addOption(OptionType.INTEGER, "maxparticipants", "Maximum amount of participants", true)

        jda!!.updateCommands().addCommands(sendOfficeEmbedCommand, sendManagementEmbedCommand, trainingCommand).queue()
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent){
        when(event.name){
            "sendofficeembed" -> SendOfficeEmbedCommand().execute(bot, event)
            "sendmanagementembed" -> SendManagementEmbedCommand().execute(bot, event)
            "training" -> TrainingCommand().execute(bot, event)
        }
    }
}