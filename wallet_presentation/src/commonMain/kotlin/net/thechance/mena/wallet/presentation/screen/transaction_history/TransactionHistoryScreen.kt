@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.transaction_history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.LocalDate
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.ic_arrow_left
import mena.wallet_presentation.generated.resources.ic_share
import mena.wallet_presentation.generated.resources.pick_end_date
import mena.wallet_presentation.generated.resources.pick_start_date
import mena.wallet_presentation.generated.resources.share
import mena.wallet_presentation.generated.resources.transactions_history
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.wallet.presentation.component.DatePickerBottomSheet
import net.thechance.mena.wallet.presentation.component.ErrorView
import net.thechance.mena.wallet.presentation.component.SnackBarContainer
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.screen.transaction_history.component.TransactionFilterBottomSheet
import net.thechance.mena.wallet.presentation.screen.transaction_history.component.TransactionHistoryEmpty
import net.thechance.mena.wallet.presentation.screen.transaction_history.component.TransactionsListContent
import net.thechance.mena.wallet.presentation.screen.wallet.component.ThreeDotsLoadingIndicator
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun TransactionHistoryScreen(
    viewModel: TransactionHistoryViewModel = koinViewModel(),
    onNavigateBackClicked: () -> Unit,
    navigateToTransactionDetails: (id: Uuid) -> Unit,
    navigateToExportTransaction: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            onTransactionHistoryEffect(
                effect = effect,
                onNavigateBackClicked = onNavigateBackClicked,
                navigateToTransactionDetails = navigateToTransactionDetails,
                navigateToExportTransaction = navigateToExportTransaction
            )
        }
    )

    TransactionHistoryContent(
        state = state,
        interactionListener = viewModel
    )
}

@Composable
fun TransactionHistoryContent(
    state: TransactionHistoryScreenState,
    interactionListener: TransactionHistoryInteractionListener,
) {
    WalletScaffold(
        topBar = {
            AppBar(
                title = stringResource(Res.string.transactions_history),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = stringResource(Res.string.back_button)
                    )
                },
                onLeadingClick = interactionListener::onBackClicked,
                trailingContent = {
                    Icon(
                        modifier = Modifier.clip(RoundedCornerShape(16.dp))
                            .clickable { interactionListener.onExportClicked() },
                        painter = painterResource(Res.drawable.ic_share),
                        contentDescription = Res.string.share.toString()
                    )
                }
            )
        }, overlays = {
            bottomSheet(state.isFilterVisible) {
                TransactionFilterBottomSheet(
                    uiState = state.filterState,
                    onDismiss = interactionListener::onDismissFilter,
                    onClickAddFilter = interactionListener::onApplyFilterClicked,
                    onResetClicked = interactionListener::onResetFilterClicked,
                    onTypeToggled = interactionListener::selectFilterType,
                    onStatusSelected = interactionListener::selectFilterStatus,
                    onStartDateClicked = interactionListener::onStartDateClicked,
                    onEndDateClicked = interactionListener::onEndDateClicked
                )
            }
            bottomSheet(isVisible = state.filterState.isDateBottomSheetVisible) {
                DatePickerBottomSheet(
                    defaultSelectedDate = when (state.filterState.datePickerMode) {
                        TransactionFilterState.DatePickerMode.START_DATE -> state.filterState.defaultStartDate
                        TransactionFilterState.DatePickerMode.END_DATE -> state.filterState.defaultEndDate
                    },
                    title = when (state.filterState.datePickerMode) {
                        TransactionFilterState.DatePickerMode.START_DATE -> stringResource(Res.string.pick_start_date)
                        TransactionFilterState.DatePickerMode.END_DATE -> stringResource(Res.string.pick_end_date)
                    },
                    onPickClick = { day, month, year ->
                        val pickedDate = LocalDate(year, month, day)
                        interactionListener.onPickDateClicked(pickedDate)
                    },
                    onDismiss = interactionListener::onDismissDatePicker
                )
            }
        },
        snackBar = { SnackBarContainer(snackBarState = state.snackBar) },
        errorState = state.errorState,
        onRetry = { interactionListener.onRetryLoadTransactionHistoryClicked() })
    {
        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    ThreeDotsLoadingIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            state.errorState != null ->
                ErrorView(onRetry = { interactionListener.onRetryLoadTransactionHistoryClicked() })

            state.history.isEmpty() && state.filterState.activeFilterCount == 0 -> {
                TransactionHistoryEmpty(modifier = Modifier.fillMaxSize())
            }

            else -> {
                TransactionsListContent(
                    modifier = Modifier.fillMaxSize(),
                    interactionListener = interactionListener,
                    state = state,
                )
            }
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
private fun onTransactionHistoryEffect(
    effect: TransactionHistoryEffect,
    onNavigateBackClicked: () -> Unit,
    navigateToTransactionDetails: (id: Uuid) -> Unit,
    navigateToExportTransaction: () -> Unit
) {
    when (effect) {
        TransactionHistoryEffect.NavigateBack -> onNavigateBackClicked()
        TransactionHistoryEffect.NavigateToExportTransaction -> navigateToExportTransaction()
        is TransactionHistoryEffect.NavigateToTransactionDetails -> {
            navigateToTransactionDetails(effect.id)
        }
    }
}