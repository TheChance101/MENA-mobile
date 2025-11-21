package net.thechance.mena.admin_panel.presentation.screen.mainContainer

import net.thechance.mena.admin_panel.presentation.model.SnackBarState
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.deposit
import net.thechance.mena.admin_panel.resources.dukan_requests
import net.thechance.mena.admin_panel.resources.dukans_management
import net.thechance.mena.admin_panel.resources.file_selected
import net.thechance.mena.admin_panel.resources.file_unselected
import net.thechance.mena.admin_panel.resources.shop_selected
import net.thechance.mena.admin_panel.resources.shop_unselected
import net.thechance.mena.admin_panel.resources.users_management
import net.thechance.mena.admin_panel.resources.users_selected
import net.thechance.mena.admin_panel.resources.users_unselected
import net.thechance.mena.admin_panel.resources.wallet_selected
import net.thechance.mena.admin_panel.resources.wallet_unselected
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

data class MainContainerScreenState(
    val authenticationStatus: Boolean = false,
    val isLogOutDialogShown: Boolean = false,
    val selectedSidebarTab: SelectedSidebarTab = SelectedSidebarTab.DUKAN_MANAGEMENT,
    val snackBar: SnackBarState = SnackBarState()
) {
    enum class SelectedSidebarTab(
        val title: StringResource,
        val selectedIconRes: DrawableResource,
        val unSelectedIconRes: DrawableResource
    ) {
        USERS_MANAGEMENT(
            title = Res.string.users_management,
            selectedIconRes = Res.drawable.users_selected,
            unSelectedIconRes = Res.drawable.users_unselected
        ),
        DUKAN_MANAGEMENT(
            title = Res.string.dukans_management,
            selectedIconRes = Res.drawable.shop_selected,
            unSelectedIconRes = Res.drawable.shop_unselected
        ),
        DUKAN_REQUEST(
            title = Res.string.dukan_requests,
            selectedIconRes = Res.drawable.file_selected,
            unSelectedIconRes = Res.drawable.file_unselected
        ),
        DEPOSIT(
            title = Res.string.deposit,
            selectedIconRes = Res.drawable.wallet_selected,
            unSelectedIconRes = Res.drawable.wallet_unselected
        )
    }
}
