package net.thechance.mena.admin_panel.presentation.screen.dukan_details

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import net.thechance.mena.admin_panel.navigation.DukanManagement
import net.thechance.mena.admin_panel.navigation.LocalNavController
import net.thechance.mena.admin_panel.presentation.component.PanelScaffold
import net.thechance.mena.admin_panel.presentation.component.SnackBarContainer
import net.thechance.mena.admin_panel.presentation.screen.dukan_details.component.DeactivateDukanDialog
import net.thechance.mena.admin_panel.presentation.screen.dukan_details.component.DukanDetailsAppBar
import net.thechance.mena.admin_panel.presentation.screen.dukan_details.component.DukanDetailsInCompactMode
import net.thechance.mena.admin_panel.presentation.screen.dukan_details.component.DukanDetailsInFullScreenMode
import net.thechance.mena.admin_panel.presentation.utils.ObserveAsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun DukanDetailsScreen(
    viewModel: DukanDetailsViewModel = koinViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val adminPanelNavController = LocalNavController.current

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect -> onDukanDetailsEffect(effect, adminPanelNavController) }
    )

    DukanDetailsScreenContent(
        state = state,
        interactionListener = viewModel
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun DukanDetailsScreenContent(
    state: DukanDetailsScreenState,
    interactionListener: DukanDetailsInteractionListener
){
    PanelScaffold(
        topBar = {
            DukanDetailsAppBar(
                onBackBtnClicked = interactionListener::onBackButtonClicked,
                dukanStatus = state.dukan.dukanStatus,
                onChangeDukanStatusBtnClicked =
                    interactionListener::onChangeDukanStatusButtonClicked,
                isActiveDukanButtonLoading = state.isActiveDukanLoading
            )
        },
        overlays = {
            dialog(state.isDeactivateDukanDialogShown){
                DeactivateDukanDialog(
                    isVisible = it,
                    onDismiss = interactionListener::onDeactivateDukanDialogDismissed,
                    onDeactivationConfirmed =
                        interactionListener::onDukanDeactivationButtonClicked,
                    deactivationReason = state.deactivateReason,
                    onReasonChanged = interactionListener::onDeactivateReasonChanged,
                    isDeactivateButtonEnabled = state.isDeactivateBtnEnabled,
                    isDeactivateButtonLoading = state.isDeactivateBtnLoading,
                )
            }
        },
        snackBar = { SnackBarContainer(snackBarState = state.snackBar) },
        errorState = state.errorState,
        onRetry = interactionListener::onRetry
    ) {

        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ){
            val isCompact = maxWidth < 800.dp

            AnimatedContent(
                targetState = isCompact,
                transitionSpec = {
                    ContentTransform(
                        targetContentEnter = fadeIn(tween(300)),
                        initialContentExit = fadeOut(tween(300))
                    )
                },
                label = "layoutTransition"
            ) { compact ->
                if (compact) {
                    DukanDetailsInCompactMode(state = state, interactionListener = interactionListener)
                }
                else{
                    DukanDetailsInFullScreenMode(state = state, interactionListener = interactionListener)
                }
            }
        }
    }
}

private fun onDukanDetailsEffect(
    effect: DukanDetailEffect,
    navController: NavController
) {
    when (effect) {
        DukanDetailEffect.NavigateBack -> navController.navigate(DukanManagement)
    }
}