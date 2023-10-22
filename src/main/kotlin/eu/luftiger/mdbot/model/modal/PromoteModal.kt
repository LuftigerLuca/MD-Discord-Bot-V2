package eu.luftiger.mdbot.model.modal

import eu.luftiger.mdbot.Bot
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.InteractionType
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import net.dv8tion.jda.api.interactions.modals.Modal

class PromoteModal(private val bot: Bot): ListenerAdapter() {

    fun reply(event: GenericComponentInteractionCreateEvent){
        if(!bot.guildsProvider.hasPermission(event.guild!!.id, event.user.id, "promote")){
            event.reply("Du hast keine Berechtigung, um eine Beförderung zu erstellen!").setEphemeral(true).queue()
            return
        }

        val userSelectionInputBuilder = StringSelectMenu.create("user")
        userSelectionInputBuilder.addOption("Test", "test")
        /*event.guild!!.loadMembers().onSuccess {
            it.forEach { member ->
                userSelectionInputBuilder.addOption(member.user.asTag, member.id)
            }
        }*/


        val rankSelectionInputBuilder = StringSelectMenu.create("rank")
        event.guild!!.roles.forEach { role ->
            rankSelectionInputBuilder.addOption(role.name, role.id)
        }

        val modal = Modal.create("promote", "Beförderung beantragen")
            .addActionRows(ActionRow.of(userSelectionInputBuilder.build()))

        event.replyModal(modal.build()).queue()
    }

    override fun onModalInteraction(event: ModalInteractionEvent) {
        if(event.modalId != "promote") return
        if(event.type != InteractionType.MODAL_SUBMIT) return

        val guild = event.guild

        if(event.guild == null) {
            event.reply("Dieser Befehl kann nur auf einem Server ausgeführt werden!").setEphemeral(true).queue()
            return
        }
    }
}