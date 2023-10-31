package eu.luftiger.mdbot.discord.event

import eu.luftiger.mdbot.discord.DiscordBot
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class MemberJoinListener(private val discordBot: DiscordBot) : ListenerAdapter() {

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val botGuildSettings = discordBot.guildsProvider.getGuild(event.guild.id)?.guildSettings ?: return

        if (botGuildSettings.isWelcomeMessageEnabled && botGuildSettings.welcomeMessage != null && botGuildSettings.welcomeMessageChannelId != null) {
            val channel = event.guild.getTextChannelById(botGuildSettings.welcomeMessageChannelId)!!
            channel.sendMessage(botGuildSettings.welcomeMessage.replace("%user%", event.user.asMention)).queue()
        }

        if (botGuildSettings.isDefaultRoleEnabled) {
            botGuildSettings.defaultRoleIds.forEach { roleId ->
                val role = event.guild.getRoleById(roleId) ?: return@forEach
                event.guild.addRoleToMember(event.member, role).queue()
            }
        }
    }
}