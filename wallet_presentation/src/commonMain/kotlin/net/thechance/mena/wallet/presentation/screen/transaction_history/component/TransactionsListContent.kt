@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.transaction_history.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.retry
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.screen.transaction_history.TransactionHistoryInteractionListener
import net.thechance.mena.wallet.presentation.screen.transaction_history.TransactionHistoryScreenState
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun TransactionsListContent(
    interactionListener: TransactionHistoryInteractionListener,
    state: TransactionHistoryScreenState,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .background(Theme.colorScheme.background.surface)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        state = listState
    ) {
        item {
            FilterButton(
                activeFilterCount = state.filterState.activeFilterCount,
                hasActiveFilters = state.filterState.hasActiveFilters,
                onClick = interactionListener::onFilterClicked
            )
        }
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
                TransactionHistoryDivider()
            }
            item {
                if (state.isPaginationLoading) {
                    TransactionLoadingState(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    )
                }
            if (state.isError != null && state.history.isNotEmpty()) {
                    PrimaryButton(
                        modifier = Modifier
                            .padding(top = Theme.spacing._12)
                            .wrapContentSize(),
                        text = stringResource(Res.string.retry),
                        onClick = interactionListener::onRetry,
                        contentPadding = PaddingValues(
                            vertical = Theme.spacing._8,
                            horizontal = Theme.spacing._16
                        )
                    )
                } //TODO: Replace with correct error view
            }
        }
    }
}