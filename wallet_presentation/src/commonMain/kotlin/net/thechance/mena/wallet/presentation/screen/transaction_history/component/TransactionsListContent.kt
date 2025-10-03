@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.transaction_history.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.error
import mena.wallet_presentation.generated.resources.filter
import mena.wallet_presentation.generated.resources.ic_filter
import mena.wallet_presentation.generated.resources.retry
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.screen.transaction_history.TransactionHistoryInteractionListener
import net.thechance.mena.wallet.presentation.screen.transaction_history.TransactionHistoryScreenState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun TransactionsListContent(
    interactionListener: TransactionHistoryInteractionListener,
    state: TransactionHistoryScreenState,
    listState: LazyListState,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surface)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        state = listState
    ) {
        item {
            FilterButton(
                onFilterClicked = interactionListener::onFilterClicked,
            )
        }

        items(state.history) { transaction ->
            TransactionHistoryCard(
                transaction = transaction,
                onTransactionCardClicked = {
                    interactionListener.onTransactionCardClicked(transaction.id)
                }
            )
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth(1f)
                    .height(1.dp)
                    .background(Theme.colorScheme.stroke)
            )
        }
        item {
            if (state.isPaginationLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    net.thechance.mena.designsystem.presentation.component.indicator.DotsProgressIndicator(
                        colors = listOf(
                            Theme.colorScheme.stroke,
                            Theme.colorScheme.shadeTertiary,
                            Theme.colorScheme.primary.primary
                        ),
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
            if (state.isError != null && state.history.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Theme.spacing._16),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = state.isError.message
                            ?: stringResource(Res.string.error),
                        style = Theme.typography.body.small,
                        color = Theme.colorScheme.error
                    )
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
@Composable
fun FilterButton(
    onFilterClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
        onClick = onFilterClicked,
        containerColor = Theme.colorScheme.brand.brandVariant,
        shape = CircleShape,
    ) {
        Icon(
            modifier = Modifier.size(16.dp),
            painter = painterResource(Res.drawable.ic_filter),
            contentDescription = stringResource(Res.string.filter)
        )
        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = stringResource(Res.string.filter),
            style = Theme.typography.label.small,
            color = Theme.colorScheme.primary.primary
        )
    }
}