package eu.luftiger.mdbot.discord.model

data class BotMember(val id: String, val permissions: List<String>) {

    fun hasPermission(permission: String): Boolean {
        return permissions.contains(permission)
    }
}