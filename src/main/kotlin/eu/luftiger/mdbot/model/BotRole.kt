package eu.luftiger.mdbot.model

data class BotRole(val id: String, val permissions: List<String>) {

    fun hasPermission(permission: String): Boolean {
        return permissions.contains(permission)
    }
}