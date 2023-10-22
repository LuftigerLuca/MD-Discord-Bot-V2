package eu.luftiger.mdbot.provider

import eu.luftiger.mdbot.Bot
import eu.luftiger.mdbot.model.BotGuild
import eu.luftiger.mdbot.model.BotGuildSettings
import eu.luftiger.mdbot.model.BotMember
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

class GuildProvider(private val bot : Bot) {


    private val databaseQueryHandler = bot.databaseQueryHandler
    var botGuilds = mutableListOf<BotGuild>()

    lateinit var executor: ScheduledExecutorService


    fun loadGuilds() {
        val guilds = databaseQueryHandler.getGuilds()
        guilds.forEach { guild ->
            val members = databaseQueryHandler.getBotMembers(guild.id).toMutableList()
            val roles = databaseQueryHandler.getBotRoles(guild.id).toMutableList()

            var guildSettings = databaseQueryHandler.getGuildSettings(guild.id)
            if(guildSettings == null) guildSettings = BotGuildSettings(null, null, null, false, null, null, false, listOf())

            botGuilds.add(BotGuild(guild.id, guild.name, guildSettings, members, roles))
        }
    }

    fun updateGuild(guild: BotGuild){
        val oldGuild = getGuild(guild.id)
        guild.members.plus(oldGuild!!.members)
        guild.roles.plus(oldGuild.roles)

        botGuilds.minus(oldGuild)
        botGuilds.plus(guild)

        databaseQueryHandler.setGuild(guild)
    }

    fun addGuild(guild: BotGuild) {
        botGuilds.plus(guild)
        databaseQueryHandler.setGuild(guild)
        databaseQueryHandler.setGuildSettings(guild.id, guild.guildSettings!!)
    }

    fun removeGuild(guild: BotGuild) {
        botGuilds.minus(guild)
        databaseQueryHandler.removeGuild(guild)
    }

    fun getGuild(guildId: String) : BotGuild? {
        return botGuilds.find { guild -> guild.id == guildId }
    }

    fun hasPermission(guildId: String, userId: String, permission: String) : Boolean {
        val botGuild = getGuild(guildId) ?: return false
        val guild = bot.jda!!.getGuildById(guildId) ?: return false
        val botMember = botGuild.members.find { member -> member.id == userId }
        if(botMember != null && botMember.hasPermission(permission)) return true

        for (role in guild.roles) {
            val botRole = botGuild.roles.find { botRole -> botRole.id == role.id }
            if(botRole != null && botRole.hasPermission(permission)) return true
        }

        return false
    }

    fun runUpdater() {
        val runnable = Runnable {
            for (guild in bot.jda!!.guilds) {

                val botGuild = getGuild(guild.id)
                if (botGuild == null) addGuild(BotGuild(guild.id, guild.name, null, mutableListOf(), mutableListOf()))
                if(botGuild != null) updateGuild(BotGuild(guild.id, guild.name, botGuild.guildSettings, mutableListOf(), mutableListOf()))

            }

            for (guild in botGuilds) {
                if (bot.jda!!.getGuildById(guild.id) == null) removeGuild(guild)
            }
        }
        executor = Executors.newSingleThreadScheduledExecutor()
        executor.scheduleAtFixedRate(runnable, 0, 1, java.util.concurrent.TimeUnit.MINUTES)
    }

    fun stopUpdater() {
        executor.shutdown()
    }
}