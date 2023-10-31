package eu.luftiger.mdbot.discord

import eu.luftiger.mdbot.MDBot
import eu.luftiger.mdbot.discord.command.CommandHandler
import eu.luftiger.mdbot.discord.event.MemberJoinListener
import eu.luftiger.mdbot.discord.model.embed.*
import eu.luftiger.mdbot.discord.model.modal.PromoteModal
import eu.luftiger.mdbot.discord.model.modal.SickNoteModal
import eu.luftiger.mdbot.discord.model.modal.SignOffModal
import eu.luftiger.mdbot.discord.provider.GuildProvider
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy


class DiscordBot(val bot: MDBot) {
    private val logger = bot.logger
    private val configuration = bot.configuration

    var jda: JDA? = null
    lateinit var guildsProvider: GuildProvider

    lateinit var officeEmbed: OfficeEmbed
    lateinit var managementOfficeEmbed: ManagementOfficeEmbed
    lateinit var sickNoteEmbed: SickNoteEmbed
    lateinit var signOffEmbed: SignOffEmbed
    lateinit var trainingEmbed: TrainingEmbed

    lateinit var signOffModal: SignOffModal
    lateinit var sickNoteModal: SickNoteModal
    lateinit var promoteModal: PromoteModal

    fun run() {
        logger.info("loading embeds and modals...")
        officeEmbed = OfficeEmbed(this)
        managementOfficeEmbed = ManagementOfficeEmbed(this)
        sickNoteEmbed = SickNoteEmbed(this)
        signOffEmbed = SignOffEmbed(this)
        trainingEmbed = TrainingEmbed(this)
        signOffModal = SignOffModal(this)
        sickNoteModal = SickNoteModal(this)
        promoteModal = PromoteModal(this)

        logger.info("loading jda...")
        jda = JDABuilder.createDefault(configuration.bot.token)
            .enableIntents(GatewayIntent.GUILD_MEMBERS)
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .setActivity(Activity.of(Activity.ActivityType.valueOf(configuration.bot.activitytype.uppercase()), configuration.bot.activityname))
            .setStatus(OnlineStatus.valueOf(configuration.bot.status.uppercase()))
            .addEventListeners(
                CommandHandler(this),
                officeEmbed,
                managementOfficeEmbed,
                sickNoteEmbed,
                trainingEmbed,
                signOffModal,
                sickNoteModal,
                signOffEmbed,
                promoteModal,
                MemberJoinListener(this)
            )
            .build()

        jda!!.awaitReady()

        logger.info("registering commands...")
        CommandHandler(this).registerCommands()

        logger.info("loading guilds...")
        guildsProvider = GuildProvider(this)
        guildsProvider.loadGuilds()
        guildsProvider.runUpdater()
    }

    fun reload() {
        shutdown()
        run()
    }

    fun shutdown() {
        jda!!.shutdown()
    }
}