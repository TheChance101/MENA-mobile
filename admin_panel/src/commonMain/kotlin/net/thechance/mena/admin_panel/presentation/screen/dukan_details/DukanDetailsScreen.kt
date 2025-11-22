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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.admin_panel.navigation.DukanManagement
import net.thechance.mena.admin_panel.navigation.LocalNavController
import net.thechance.mena.admin_panel.presentation.component.PanelScaffold
import net.thechance.mena.admin_panel.presentation.component.SnackBarContainer
import net.thechance.mena.admin_panel.presentation.screen.dukan_details.component.DukanDetailsAppBar
import net.thechance.mena.admin_panel.presentation.screen.dukan_details.component.DukanDetailsInCompactMode
import net.thechance.mena.admin_panel.presentation.screen.dukan_details.component.DukanDetailsInFullScreenMode
import net.thechance.mena.admin_panel.presentation.component.DukanStatusChangeDialog
import net.thechance.mena.admin_panel.presentation.utils.ObserveAsEffect
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.deactivate
import net.thechance.mena.admin_panel.resources.deactivate_dukan_content
import net.thechance.mena.admin_panel.resources.deactivate_dukan_header
import net.thechance.mena.admin_panel.resources.deactivate_dukan_reason
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun DukanDetailsScreen(
    viewModel: DukanDetailsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var isMapVisible by remember { mutableStateOf(true) }
    val adminPanelNavController = LocalNavController.current

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            onDukanDetailsEffect(
                effect = effect,
                onNavigateBack = {
                    isMapVisible = false
                    adminPanelNavController.navigate(DukanManagement)
                }
            )
        }
    )

    DukanDetailsScreenContent(
        state = state,
        interactionListener = viewModel,
        isMapVisible = isMapVisible,
        onBackClick = {
            isMapVisible = false
            viewModel.onBackButtonClicked()
        }
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun DukanDetailsScreenContent(
    state: DukanDetailsScreenState,
    interactionListener: DukanDetailsInteractionListener,
    isMapVisible: Boolean,
    onBackClick: () -> Unit
) {
    val isAnyDialogVisible = state.isDeactivateDukanDialogShown

    PanelScaffold(
        topBar = {
            DukanDetailsAppBar(
                onBackBtnClicked = onBackClick,
                dukanStatus = state.dukan.dukanStatus,
                onChangeDukanStatusBtnClicked = interactionListener::onChangeDukanStatusButtonClicked,
                isActiveDukanButtonLoading = state.isActiveDukanLoading
            )
        },
        overlays = {
            dialog(state.isDeactivateDukanDialogShown) {
                DukanStatusChangeDialog(
                    isVisible = it,
                    onDismiss = interactionListener::onDeactivateDukanDialogDismissed,
                    onConfirmed = interactionListener::onDukanDeactivationButtonClicked,
                    reason = state.deactivateReason,
                    onReasonChanged = interactionListener::onDeactivateReasonChanged,
                    title = stringResource(Res.string.deactivate_dukan_header),
                    description = stringResource(Res.string.deactivate_dukan_content),
                    reasonLabel = stringResource(Res.string.deactivate_dukan_reason),
                    confirmButtonText = stringResource(Res.string.deactivate),
                    isConfirmButtonEnabled = state.isDeactivateBtnEnabled,
                    isConfirmButtonLoading = state.isDeactivateBtnLoading
                )
            }
        },
        snackBar = { SnackBarContainer(snackBarState = state.snackBar) },
        errorState = state.errorState,
        onRetry = interactionListener::onRetry
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
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
                    DukanDetailsInCompactMode(
                        state = state,
                        interactionListener = interactionListener,
                        isMapVisible = isMapVisible && !isAnyDialogVisible
                    )
                } else {
                    DukanDetailsInFullScreenMode(
                        state = state,
                        interactionListener = interactionListener,
                        isMapVisible = isMapVisible && !isAnyDialogVisible
                    )
                }
            }
        }
    }
}

private fun onDukanDetailsEffect(
    effect: DukanDetailEffect,
    onNavigateBack: () -> Unit
) {
    when (effect) {
        DukanDetailEffect.NavigateBack -> onNavigateBack()
    }
}