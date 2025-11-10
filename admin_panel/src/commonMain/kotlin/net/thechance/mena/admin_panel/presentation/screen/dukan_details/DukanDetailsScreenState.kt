package net.thechance.mena.admin_panel.presentation.screen.dukan_details

import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.activate_dukan
import net.thechance.mena.admin_panel.resources.deactivate_dukan
import net.thechance.mena.admin_panel.resources.ic_activate
import net.thechance.mena.admin_panel.resources.ic_block
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

data class DukanDetailsScreenState(
    val isLoading: Boolean = false,
    val dukanStatus: DukanStatus = DukanStatus.DEACTIVE
){
    enum class DukanStatus(
        val text: StringResource,
        val icon: DrawableResource
    ){
        DEACTIVE(text = Res.string.activate_dukan, icon = Res.drawable.ic_activate),
        ACTIVE(text = Res.string.deactivate_dukan, icon = Res.drawable.ic_block)
    }
}