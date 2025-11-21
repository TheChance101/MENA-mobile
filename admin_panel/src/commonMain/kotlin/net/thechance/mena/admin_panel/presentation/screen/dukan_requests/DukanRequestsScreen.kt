package net.thechance.mena.admin_panel.presentation.screen.dukan_requests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.admin_panel.presentation.component.DukansCounter
import net.thechance.mena.admin_panel.presentation.component.EmptyDukansState
import net.thechance.mena.admin_panel.presentation.component.PanelScaffold
import net.thechance.mena.admin_panel.presentation.component.SnackBarContainer
import net.thechance.mena.admin_panel.presentation.screen.dukan_requests.component.DukanDetailsDrawerView
import net.thechance.mena.admin_panel.presentation.screen.dukan_requests.component.DukanRequestsTableContent
import net.thechance.mena.admin_panel.presentation.screen.dukan_requests.component.RejectionDukanDialog
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.dukan_requests
import net.thechance.mena.admin_panel.resources.requests
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun DukanRequestsScreen(viewModel: DukanRequestsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DukanRequestsScreenContent(state = state, listener = viewModel)
}

@OptIn(ExperimentalUuidApi::class)
@Composable
private fun DukanRequestsScreenContent(
    state: DukanRequestsScreenState,
    listener: DukanRequestsInteractionListener
) {
    PanelScaffold(
        topBar = { DukanRequestsScreenTopBar() },
        overlays = {
            dialog(state.isRejectDialogShown) {
                RejectionDukanDialog(
                    isVisible = it,
                    onDismiss = listener::onRejectDukanDialogDismissed,
                    onRejectionConfirmed = listener::onRejectDukanConfirmed,
                    rejectionReason = state.rejectReason,
                    onReasonChanged = listener::onRejectionMessageChanged,
                    isRejectButtonEnabled = state.isRejectButtonEnabled,
                    isRejectButtonLoading = state.isRejectButtonLoading,
                )
            }
        },
        isLoading = state.isInitialLoading,
        snackBar = { SnackBarContainer(snackBarState = state.snackBar) },
        errorState = state.errorState,
        onRetry = listener::onRetryClicked
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            DukansCounter(
                title = stringResource(Res.string.requests),
                count = state.totalDukanRequests,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
            )
            when {
                state.dukans.isEmpty() -> EmptyDukansState(
                    modifier = Modifier.fillMaxSize().offset(y = -(76.dp))
                )

                else -> DukanRequestsTableContent(
                    state = state,
                    listener = listener,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    if (state.selectedDukan != null) {
        DukanDetailsDrawerView(
            isOpen = state.isDukanDetailsShown,
            onDismiss = listener::onDukanDetailsDismissed,
            selectedDukanItem = state.selectedDukan,
            onRejectDukanClicked = listener::onRejectDukanClicked,
            onApproveDukanClicked = listener::onApproveDukanClicked,
        )
    }
}

@Composable
private fun DukanRequestsScreenTopBar() {
    AppBar(
        title = stringResource(Res.string.dukan_requests),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
        modifier = Modifier.background(Theme.colorScheme.background.surfaceLow)
    )
}