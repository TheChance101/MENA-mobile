@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.transaction_history.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.retry
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.model.FilterStatus
import net.thechance.mena.wallet.presentation.model.FilterType
import net.thechance.mena.wallet.presentation.screen.transaction_history.TransactionFilterState
import net.thechance.mena.wallet.presentation.screen.transaction_history.TransactionHistoryInteractionListener
import net.thechance.mena.wallet.presentation.screen.transaction_history.TransactionHistoryScreenState
import net.thechance.mena.wallet.presentation.utils.PaginationTrigger
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun TransactionsListContent(
    interactionListener: TransactionHistoryInteractionListener,
    state: TransactionHistoryScreenState,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    PaginationTrigger(
        list = state.history,
        listState = listState,
        buffer = 2,
        loadNextItems = interactionListener::onNextPageRequested
    )

    Column (
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (state.history.isNotEmpty() || state.filterState.activeFilterCount != 0) {
            FilterButton(
                activeFilterCount = state.filterState.activeFilterCount,
                onClick = interactionListener::onFilterClicked,
                modifier = Modifier.padding(top = 12.dp).padding(horizontal = 16.dp)
            )
        }

        LazyColumn(
            modifier = modifier
                .background(Theme.colorScheme.background.surface)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = listState
        ) {

            if (state.history.isEmpty() && state.filterState.activeFilterCount > 0) {
                item {
                    FilterTransactionEmpty(modifier = Modifier.fillParentMaxSize())
                }
            }

            if (state.history.isNotEmpty()) {
                items(state.history) { transaction ->
                    TransactionHistoryCard(
                        transaction = transaction,
                        onTransactionCardClicked = {
                            interactionListener.onTransactionCardClicked(transaction.id)
                        }
                    )

                    if (state.history.last() != transaction) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Theme.colorScheme.stroke)
                        )
                    }
                }

                if (state.isPaginationLoading) {
                    item {
                        TransactionLoadingState(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        )
                    }
                }

                if (state.errorState != null && state.history.isNotEmpty()) {
                    item {
                        PrimaryButton(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .wrapContentSize(),
                            text = stringResource(Res.string.retry),
                            onClick = interactionListener::onRetryLoadTransactionHistoryClicked,
                            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Preview(showBackground = true)
@Composable
private fun TransactionsListContentPreview() {
    val mockTransactions = listOf(
        TransactionHistoryScreenState.TransactionHistoryUiState(
            id = Uuid.parse("123e4567-e89b-12d3-a456-426614174000"),
            timeAndDate = "21 Oct 2025 - 10:00 AM",
            amount = "$150.00",
            type = TransactionHistoryScreenState.TransactionTypeUiState.ONLINE_SHOPPING,
            status = TransactionHistoryScreenState.TransactionStatusUiState.SUCCESS,
            contactName = "Ahmed Ali"
        ),
        TransactionHistoryScreenState.TransactionHistoryUiState(
            id = Uuid.parse("223e4567-e89b-12d3-a456-426614174111"),
            timeAndDate = "20 Oct 2025 - 09:30 AM",
            amount = "$200.00",
            type = TransactionHistoryScreenState.TransactionTypeUiState.SENT,
            status = TransactionHistoryScreenState.TransactionStatusUiState.FAILED,
            contactName = "Fatma Zahra"
        ),
        TransactionHistoryScreenState.TransactionHistoryUiState(
            id = Uuid.parse("323e4567-e89b-12d3-a456-426614174222"),
            timeAndDate = "19 Oct 2025 - 08:45 AM",
            amount = "$75.00",
            type = TransactionHistoryScreenState.TransactionTypeUiState.RECEIVED,
            status = TransactionHistoryScreenState.TransactionStatusUiState.SUCCESS,
            contactName = "Mohamed Salah"
        )
    )

    val mockState = TransactionHistoryScreenState(
        history = mockTransactions,
        filterState = TransactionFilterState(
            activeFilterCount = 1,
        ),
        isPaginationLoading = false,
        errorState = null
    )

    val mockListener = object : TransactionHistoryInteractionListener {
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

    MenaTheme {
        TransactionsListContent(
            interactionListener = mockListener,
            state = mockState,
            modifier = Modifier.padding(16.dp)
        )
    }
}