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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.retry
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.screen.transaction_history.TransactionHistoryInteractionListener
import net.thechance.mena.wallet.presentation.screen.transaction_history.TransactionHistoryScreenState
import net.thechance.mena.wallet.presentation.utils.PaginationTrigger
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.ExperimentalUuidApi

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
    ){
        if (state.history.isNotEmpty() || state.filterState.activeFilterCount != 0) {
            FilterButton(
                activeFilterCount = state.filterState.activeFilterCount,
                hasActiveFilters = state.filterState.hasActiveFilters,
                onClick = interactionListener::onFilterClicked,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        LazyColumn(
            modifier = modifier
                .background(Theme.colorScheme.background.surface)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
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
                                .padding(top = 4.dp)
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
                                .padding(top = Theme.spacing._12)
                                .wrapContentSize(),
                            text = stringResource(Res.string.retry),
                            onClick = interactionListener::onRetryLoadTransactionHistoryClicked,
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
}