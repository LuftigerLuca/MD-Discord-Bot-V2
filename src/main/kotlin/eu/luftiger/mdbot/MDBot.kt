package eu.luftiger.mdbot

import eu.luftiger.discordbotkore.database.DataSourceProvider
import eu.luftiger.discordbotkore.database.DatabaseSetup
import eu.luftiger.mdbot.common.configuration.Configuration
import eu.luftiger.mdbot.common.configuration.ConfigurationHandler
import eu.luftiger.mdbot.common.database.DatabaseQueryHandler
import eu.luftiger.mdbot.discord.DiscordBot
import java.util.logging.Logger
import javax.sql.DataSource

object MDBot {
    val logger: Logger = Logger.getLogger("MDBot")

    lateinit var configuration: Configuration
    private lateinit var dataSource: DataSource
    lateinit var databaseQueryHandler: DatabaseQueryHandler

    lateinit var discordbot: DiscordBot

    @JvmStatic
    fun main(args: Array<String>) {
        run()
    }

    private fun run() {
        logger.info("Starting bot...")

        logger.info("loading configuration...")
        configuration = ConfigurationHandler().loadConfiguration()

        logger.info("loading database...")
        dataSource = DataSourceProvider.initMySQLDataSource(
            configuration.database.username,
            configuration.database.password,
            configuration.database.host,
            configuration.database.port.toString(),
            configuration.database.database
        )
        DatabaseSetup.initDatabase(logger, dataSource)
        databaseQueryHandler = DatabaseQueryHandler(dataSource)

        logger.info("loading discordbot...")
        discordbot = DiscordBot(this)
        discordbot.run()
        logger.info("Bot started!")


        logger.info("Type 'exit' to stop the bot.")
        var exit = false
        while (!exit) {
            val input = readlnOrNull()
            when (input!!.lowercase()) {
                "exit", "stop" -> {
                    exit = true
                    logger.info("Stopping bot...")
                    discordbot.shutdown()
                    logger.info("Bot stopped!")
                }

                "reload" -> {
                    logger.info("Reloading configuration...")
                    configuration = ConfigurationHandler().loadConfiguration()
                    logger.info("Configuration reloaded!")
                    logger.info("Reloading jda...")
                    discordbot.reload()
                    logger.info("JDA reloaded!")
                }

                else -> {
                    logger.info("Unknown command!")
                }
            }
        }
    }
}