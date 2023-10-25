package eu.luftiger.mdbot

import eu.luftiger.mdbot.command.CommandHandler
import eu.luftiger.mdbot.configuration.Configuration
import eu.luftiger.mdbot.configuration.ConfigurationHandler
import eu.luftiger.mdbot.database.DataSourceProvider
import eu.luftiger.mdbot.database.DatabaseQueryHandler
import eu.luftiger.mdbot.database.DatabaseSetup
import eu.luftiger.mdbot.event.MemberJoinListener
import eu.luftiger.mdbot.model.embed.*
import eu.luftiger.mdbot.model.modal.PromoteModal
import eu.luftiger.mdbot.model.modal.SickNoteModal
import eu.luftiger.mdbot.model.modal.SignOffModal
import eu.luftiger.mdbot.provider.GuildProvider
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import java.util.logging.Logger
import javax.sql.DataSource



object Bot {
    private val logger : Logger = Logger.getLogger("Bot")

    lateinit var configuration: Configuration
    private lateinit var dataSource: DataSource
    lateinit var databaseQueryHandler: DatabaseQueryHandler
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



    @JvmStatic
    fun main(args: Array<String>) {
        run()
    }

    private fun run(){
        logger.info("Starting bot...")

        logger.info("loading configuration...")
        configuration = ConfigurationHandler().loadConfiguration()

        logger.info("loading database...")
        dataSource = DataSourceProvider.initMySQLDataSource(logger, configuration)
        DatabaseSetup.initDatabase(logger, dataSource)
        databaseQueryHandler = DatabaseQueryHandler(dataSource)

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


        logger.info("Bot started! Type 'stop' to stop the bot.")

        var waitForInput = true
        while(waitForInput){
            when(readlnOrNull()){
                "reload" -> {
                    waitForInput = false
                    reload()
                }

                "stop", "exit", "quit" -> {
                    logger.info("Stopping bot...")
                    waitForInput = false
                    jda!!.shutdown()
                }
                else -> logger.info("Unknown command!")
            }
        }
    }

    private fun reload(){
        logger.info("Reloading bot...")
        jda!!.shutdown()
        run()
    }
}