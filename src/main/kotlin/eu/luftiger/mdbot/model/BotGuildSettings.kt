package eu.luftiger.mdbot.model

class BotGuildSettings(val signOffChannelId : String?,
                       val sickNoteChannelId : String?,
                       val promoteInfoChannelId: String?,
                       val isWelcomeMessageEnabled: Boolean,
                       val welcomeMessageChannelId: String?,
                       val welcomeMessage: String?,
                       val isDefaultRoleEnabled: Boolean,
                       val defaultRoleIds: List<String>) {
}