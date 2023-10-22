package eu.luftiger.mdbot.model

data class BotGuild(val id : String, val name : String, val guildSettings: BotGuildSettings?, val members: MutableList<BotMember>, val roles: MutableList<BotRole>) {

}
