@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.transaction_history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.datetime.LocalDate
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.ic_share
import mena.wallet_presentation.generated.resources.share
import mena.wallet_presentation.generated.resources.transactions_history
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.component.BackIcon
import net.thechance.mena.wallet.presentation.component.DatePickerBottomSheet
import net.thechance.mena.wallet.presentation.component.SnackBarContainer
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.model.FilterStatus
import net.thechance.mena.wallet.presentation.model.FilterType
import net.thechance.mena.wallet.presentation.navigation.ExportTransactionsScreenRoute
import net.thechance.mena.wallet.presentation.navigation.LocalNavController
import net.thechance.mena.wallet.presentation.navigation.TransactionDetailsScreenRoute
import net.thechance.mena.wallet.presentation.screen.transaction_history.component.TransactionFilterBottomSheet
import net.thechance.mena.wallet.presentation.screen.transaction_history.component.TransactionHistoryEmpty
import net.thechance.mena.wallet.presentation.screen.transaction_history.component.TransactionsListContent
import net.thechance.mena.wallet.presentation.utils.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun TransactionHistoryScreen(viewModel: TransactionHistoryViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect ->
            onTransactionHistoryEffect(effect = effect, navController = navController)
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
                leadingContent = { BackIcon() },
                onLeadingClick = interactionListener::onBackClicked,
                trailingContent = {
                    if (state.history.isNotEmpty() || state.filterState.activeFilterCount > 0) {
                        ExportIcon(interactionListener = interactionListener)
                    }
                }
            )
        }, overlays = {
            bottomSheet(state.isFilterVisible) { isVisible ->
                TransactionFilterBottomSheet(
                    isVisible = isVisible,
                    uiState = state.filterState,
                    onDismiss = interactionListener::onDismissFilter,
                    onClickAddFilter = interactionListener::onApplyFilterClicked,
                    onResetClicked = interactionListener::onResetFilterClicked,
                    onTypeToggled = interactionListener::onFilterTypeSelected,
                    onStatusSelected = interactionListener::onFilterStatusSelected,
                    onStartDateClicked = interactionListener::onStartDateClicked,
                    onEndDateClicked = interactionListener::onEndDateClicked
                )
            }
            bottomSheet(isVisible = state.filterState.isDateBottomSheetVisible) { isVisible ->
                DatePickerBottomSheet(
                    isVisible = isVisible,
                    defaultSelectedDate = state.filterState.defaultSelectedDate,
                    title = stringResource(state.filterState.datePickerMode.titleRes),
                    onPickClick = interactionListener::onPickDateClicked,
                    onDismiss = interactionListener::onDismissDatePicker
                )
            }
        },
        snackBar = { SnackBarContainer(snackBarState = state.snackBar) },
        errorState = state.errorState,
        isLoading = state.isLoading && state.filterState.isApplyButtonLoading.not(),
        onRetry = { interactionListener.onRetryLoadTransactionHistoryClicked() })
    {
        when {
            state.history.isEmpty() && state.filterState.activeFilterCount == 0 -> {
                TransactionHistoryEmpty(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                )
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
    navController: NavController
) {
    when (effect) {
        TransactionHistoryEffect.NavigateBack -> navController.popBackStack()
        TransactionHistoryEffect.NavigateToExportTransaction -> {
            navController.navigate(ExportTransactionsScreenRoute)
        }

        is TransactionHistoryEffect.NavigateToTransactionDetails -> {
            navController.navigate(TransactionDetailsScreenRoute(effect.id.toString()))
        }
    }
}

@Composable
private fun ExportIcon(
    interactionListener: TransactionHistoryInteractionListener,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(Res.drawable.ic_share),
        contentDescription = stringResource(Res.string.share),
        tint = Theme.colorScheme.shadePrimary,
        modifier = modifier
            .size(40.dp)
            .background(
                Theme.colorScheme.background.surfaceLow,
                RoundedCornerShape(Theme.radius.md)
            )
            .clip(RoundedCornerShape(Theme.radius.md))
            .clickable { interactionListener.onExportClicked() },
    )
}

@Preview(showBackground = true)
@Composable
private fun TransactionHistoryContentPreview() {
    MenaTheme {
        TransactionHistoryContent(
            state = TransactionHistoryScreenState(),
            interactionListener = object : TransactionHistoryInteractionListener {
                override fun onBackClicked() {}
                override fun onTransactionCardClicked(id: Uuid) {}
                override fun onExportClicked() {}
                override fun onFilterClicked() {}
                override fun onNextPageRequested() {}
                override fun onDismissFilter() {}
                override fun onFilterTypeSelected(type: FilterType) {}
                override fun onFilterStatusSelected(status: FilterStatus) {}
                override fun onResetFilterClicked() {}
                override fun onApplyFilterClicked() {}
                override fun onStartDateClicked() {}
                override fun onEndDateClicked() {}
                override fun onDismissDatePicker() {}
                override fun onPickDateClicked(date: LocalDate) {}
                override fun onRetryLoadTransactionHistoryClicked() {}
            }
        )
    }
}
