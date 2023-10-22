package eu.luftiger.mdbot.configuration

data class Configuration(val bot: BotConfiguration, val database: DatabaseConfiguration)

data class BotConfiguration(
    val token: String,
    val activitytype: String,
    val activityname: String,
    val status : String,
)

data class DatabaseConfiguration(
    val host: String,
    val port: Int,
    val database: String,
    val username: String,
    val password: String,
)
