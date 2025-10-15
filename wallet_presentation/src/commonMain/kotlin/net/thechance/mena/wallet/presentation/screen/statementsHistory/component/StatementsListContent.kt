@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.statementsHistory.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import net.thechance.mena.wallet.presentation.screen.statementsHistory.StatementsHistoryInteractionListener
import net.thechance.mena.wallet.presentation.screen.statementsHistory.StatementsHistoryScreenState
import net.thechance.mena.wallet.presentation.screen.transaction_history.component.TransactionLoadingState
import net.thechance.mena.wallet.presentation.utils.PaginationTrigger
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun StatementsListContent(
    listener: StatementsHistoryInteractionListener,
    state: StatementsHistoryScreenState,
    modifier: Modifier = Modifier
) {

    val listState = rememberLazyListState()

    PaginationTrigger(
        list = state.statements,
        listState = listState,
        buffer = 2,
        loadNextItems = listener::onNextPageRequested
    )

    LazyColumn(
        modifier = modifier
            .background(Theme.colorScheme.background.surface)
            .padding(horizontal = Theme.spacing._16),
        contentPadding = PaddingValues(bottom = Theme.spacing._16),
        state = listState
    ) {
        items(state.statements) { statement ->
            StatementHistoryCard(
                startDate = statement.startDate,
                endDate = statement.endDate,
                totalInflow = statement.totalInflow.toString(),
                totalOutflow = statement.totalOutflow.toString(),
                onStatementCardClicked = { listener.onStatementCardClicked(statement) }
            )

            if (state.statements.last() != statement) {
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
                        .padding(vertical = Theme.spacing._16)
                )
            }
        }

        if (state.errorState != null && state.statements.isNotEmpty()) {
            item {
                PrimaryButton(
                    modifier = Modifier
                        .padding(top = Theme.spacing._12)
                        .wrapContentSize(),
                    text = stringResource(Res.string.retry),
                    onClick = listener::onRetryLoadStatementsHistoryClicked,
                    contentPadding = PaddingValues(
                        vertical = Theme.spacing._8,
                        horizontal = Theme.spacing._16
                    )
                )
            }
        }
    }
}