package net.thechance.mena.admin_panel.presentation.screen.mainContainer.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import net.thechance.mena.admin_panel.navigation.AdminPanelNavHost
import net.thechance.mena.admin_panel.presentation.component.AdminConfirmationDialog
import net.thechance.mena.admin_panel.presentation.component.SnackBarContainer
import net.thechance.mena.admin_panel.presentation.model.SnackBarState
import net.thechance.mena.admin_panel.presentation.screen.mainContainer.MainContainerInteractionListener
import net.thechance.mena.admin_panel.presentation.screen.mainContainer.MainContainerScreenState
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.confirm_logout_icon
import net.thechance.mena.admin_panel.resources.logout
import net.thechance.mena.admin_panel.resources.logout_dialog_icon
import net.thechance.mena.admin_panel.resources.logout_disc
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AdminPanelScaffold(
    state: MainContainerScreenState,
    interactionListener: MainContainerInteractionListener,
    isLoginScreen: Boolean,
    navController: NavHostController,
    snackBarState: SnackBarState,
) {
    Scaffold(
        overlays = {
            dialog(isVisible = state.isLogOutDialogShown) {
                AdminConfirmationDialog(
                    dialogIcon = painterResource(Res.drawable.logout_dialog_icon),
                    confirmationIcon = painterResource(Res.drawable.confirm_logout_icon),
                    title = stringResource(Res.string.logout),
                    description = stringResource(Res.string.logout_disc),
                    confirmationButtonText = stringResource(Res.string.logout),
                    onDismiss = interactionListener::onLogoutDismissed,
                    onConfirm = interactionListener::onLogoutConfirmed,
                    isVisible = state.isLogOutDialogShown
                )
            }
        },
        content = {
            Row(modifier = Modifier.fillMaxWidth()) {
                AnimatedVisibility(
                    visible = !isLoginScreen,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    AdminPanelScaffoldMainContent(
                        state = state,
                        interactionListener = interactionListener,
                        snackBarState = snackBarState
                    )
                }
                AdminPanelNavHost(
                    modifier = Modifier.weight(1f),
                    navController = navController,
                    isUserLoggedIn = state.authenticationStatus
                )
            }
        }
    )
}

@Composable
private fun AdminPanelScaffoldMainContent(
    state: MainContainerScreenState,
    interactionListener: MainContainerInteractionListener,
    snackBarState: SnackBarState,
) {
    Box(
        modifier = Modifier.wrapContentSize()
    ) {
        Box(modifier = Modifier.wrapContentWidth()) {
            AdminPanelSideBar(
                modifier = Modifier.padding(bottom = 34.dp),
                selectedTab = state.selectedSidebarTab,
                interactionListener = interactionListener
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 32.dp, top = 32.dp)
        ) { SnackBarContainer(snackBarState) }
    }
}