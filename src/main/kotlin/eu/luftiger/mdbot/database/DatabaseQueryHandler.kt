package eu.luftiger.mdbot.database

import eu.luftiger.mdbot.model.BotGuild
import eu.luftiger.mdbot.model.BotGuildSettings
import eu.luftiger.mdbot.model.BotMember
import eu.luftiger.mdbot.model.BotRole
import javax.sql.DataSource

class DatabaseQueryHandler(private val dataSource: DataSource) {

    private val connection = dataSource.connection

    fun getGuilds() : List<BotGuild>{
        val statement = connection.prepareStatement("SELECT * FROM guilds")
        val resultSet = statement.executeQuery()
        val guilds = mutableListOf<BotGuild>()
        while (resultSet.next()) {
            val id = resultSet.getString("id")
            val name = resultSet.getString("name")
            val guild = BotGuild(id, name, null, mutableListOf(), mutableListOf())
            guilds.add(guild)
        }

        return guilds
    }

    fun setGuild(guild: BotGuild) {
        val statement = connection.prepareStatement("INSERT INTO guilds (id, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE name = VALUES(name)")
        statement.setString(1, guild.id)
        statement.setString(2, guild.name)
        statement.execute()
    }

    fun removeGuild(guild: BotGuild) {
        removeGuildSettings(guild.id)
        removeBotMembers(guild.id)
        removeBotRoles(guild.id)

        val statement = connection.prepareStatement("DELETE FROM guilds WHERE id = ?")
        statement.setString(1, guild.id)
        statement.execute()

    }

    fun getGuildSettings(guildId: String) : BotGuildSettings? {
        val statement = connection.prepareStatement("SELECT * FROM guild_settings WHERE guild_id = ?")
        statement.setString(1, guildId)
        val resultSet = statement.executeQuery()
        if (resultSet.next()) {
            val signOffChannelId = resultSet.getString("signoff_channel_id")
            val sickNoteChannelId = resultSet.getString("sicknote_channel_id")
            val promoteInfoChannelId = resultSet.getString("promote_info_channel_id")
            val isWelcomeMessageEnabled = resultSet.getBoolean("is_welcome_enabled")
            val welcomeMessageChannelId = resultSet.getString("welcome_channel_id")
            val welcomeMessage = resultSet.getString("welcome_message")
            val isDefaultRoleEnabled = resultSet.getBoolean("is_default_role_enabled")
            val defaultRoleIds = resultSet.getString("default_roles").split(",").toList()

            return BotGuildSettings(signOffChannelId, sickNoteChannelId, promoteInfoChannelId, isWelcomeMessageEnabled, welcomeMessageChannelId, welcomeMessage, isDefaultRoleEnabled, defaultRoleIds)
        }

        return null
    }

    fun setGuildSettings(guildId: String, guildSettings: BotGuildSettings) {
        val statement = connection.prepareStatement("INSERT INTO guild_settings (guild_id, signoff_channel_id, sicknote_channel_id, promote_info_channel_id, is_welcome_enabled, welcome_channel_id, welcome_message, is_default_role_enabled, default_roles) VALUES (?, ?, ?, ?, ?, ?, ? ,? ,?) ON DUPLICATE KEY UPDATE sign_off_channel_id = VALUES(sign_off_channel_id), sicknote_channel_id = VALUES(sicknote_channel_id), promote_info_channel_id = VALUES(promote_info_channel_id), is_welcome_enabled = VALUES(is_welcome_enabled), welcome_channel_id = VALUES(welcome_channel_id), welcome_message = VALUES(welcome_message), is_default_role_enabled = VALUES(is_default_role_enabled), default_roles = VALUES(default_roles)")
        statement.setString(1, guildId)
        statement.setString(2, guildSettings.signOffChannelId)
        statement.setString(3, guildSettings.sickNoteChannelId)
        statement.setString(4, guildSettings.promoteInfoChannelId)
        statement.setBoolean(5, guildSettings.isWelcomeMessageEnabled)
        statement.setString(6, guildSettings.welcomeMessageChannelId)
        statement.setString(7, guildSettings.welcomeMessage)
        statement.setBoolean(8, guildSettings.isDefaultRoleEnabled)
        statement.setString(9, guildSettings.defaultRoleIds.joinToString(","))

        statement.execute()
    }

    fun removeGuildSettings(guildId: String) {
        val statement = connection.prepareStatement("DELETE FROM guild_settings WHERE guild_id = ?")
        statement.setString(1, guildId)
        statement.execute()
    }

    fun getBotMembers(guildId: String) : List<BotMember> {
        val statement = connection.prepareStatement("SELECT * FROM members WHERE guild_id = ?")
        statement.setString(1, guildId)
        val resultSet = statement.executeQuery()
        val botMembers = mutableListOf<BotMember>()
        while (resultSet.next()) {
            val id = resultSet.getString("user_id")
            val permissions = resultSet.getString("permissions").split(",").toList()
            val botMember = BotMember(id, permissions)
            botMembers.add(botMember)
        }

        return botMembers
    }

    fun setBotMember(botMember: BotMember, guildId: String) {
        val statement = connection.prepareStatement("INSERT INTO members (guild_id, user_id, permissions) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE permissions = VALUES(permissions)")
        statement.setString(1, guildId)
        statement.setString(2, botMember.id)
        statement.setString(3, botMember.permissions.joinToString(","))
        statement.execute()
    }

    fun removeBotMember(botMember: BotMember, guildId: String) {
        val statement = connection.prepareStatement("DELETE FROM members WHERE guild_id = ? AND user_id = ?")
        statement.setString(1, guildId)
        statement.setString(2, botMember.id)
        statement.execute()
    }

    fun removeBotMembers(guildId: String) {
        val statement = connection.prepareStatement("DELETE FROM members WHERE guild_id = ?")
        statement.setString(1, guildId)
        statement.execute()
    }

    fun getBotRoles(guildId: String) : List<BotRole> {
        val statement = connection.prepareStatement("SELECT * FROM roles WHERE guild_id = ?")
        statement.setString(1, guildId)
        val resultSet = statement.executeQuery()
        val botRoles = mutableListOf<BotRole>()
        while (resultSet.next()) {
            val id = resultSet.getString("role_id")
            val permissions = resultSet.getString("permissions").split(",").toList()
            val botRole = BotRole(id, permissions)
            botRoles.add(botRole)
        }

        return botRoles
    }

    fun setBotRole(botRole: BotRole, guildId: String) {
        val statement = connection.prepareStatement("INSERT INTO roles (guild_id, role_id, permissions) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE permissions = VALUES(permissions)")
        statement.setString(1, guildId)
        statement.setString(2, botRole.id)
        statement.setString(3, botRole.permissions.joinToString(","))
        statement.execute()
    }

    fun removeBotRole(botRole: BotRole, guildId: String) {
        val statement = connection.prepareStatement("DELETE FROM roles WHERE guild_id = ? AND role_id = ?")
        statement.setString(1, guildId)
        statement.setString(2, botRole.id)
        statement.execute()
    }

    fun removeBotRoles(guildId: String) {
        val statement = connection.prepareStatement("DELETE FROM roles WHERE guild_id = ?")
        statement.setString(1, guildId)
        statement.execute()
    }
}