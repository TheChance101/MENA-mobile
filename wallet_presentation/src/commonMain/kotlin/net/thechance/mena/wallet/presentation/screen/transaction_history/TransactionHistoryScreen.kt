package net.thechance.mena.wallet.presentation.screen.transaction_history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.ic_arrow_left
import mena.wallet_presentation.generated.resources.ic_filter
import mena.wallet_presentation.generated.resources.ic_receive
import mena.wallet_presentation.generated.resources.ic_send
import mena.wallet_presentation.generated.resources.ic_share
import mena.wallet_presentation.generated.resources.ic_shopping_bag
import mena.wallet_presentation.generated.resources.my_wallet
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.chip.Chip
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.screen.transaction_history.component.TransactionHistoryCard
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun TransactionHistoryContent(
    state: List<TransactionHistoryScreenState>,
    interactionListener: TransactionHistoryInteractionListener
) {
    WalletScaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            AppBar(
                title = stringResource(Res.string.my_wallet),
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
                        painter = painterResource(Res.drawable.ic_share),
                        contentDescription = "share button"
                    )
                },
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colorScheme.background.surface)
                .padding(horizontal = 16.dp),
        ) {
            item{
                Chip(
                    text = "Filter",
                    isSelected = true,
                    onClick = interactionListener::onFilterClicked,
                    painter = painterResource(Res.drawable.ic_filter)
                )
            }
            items(state) { transaction ->
                TransactionHistoryCard(
                    transactionTypeIcon = getTransactionTypeIcon(transaction.transactionType),
                    transactionTitle = getTransactionTitle(transaction.transactionType),
                    transactionTimeAndDate = transaction.transactionTimeAndDate,
                    amount = transaction.amount,
                    transactionStatus = transaction.transactionStatus,
                    onTransactionCardClicked = interactionListener::onTransactionCardClicked
                )
            }
        }
    }
}


private fun getTransactionTypeIcon(type: TransactionHistoryScreenState.TransactionType): DrawableResource =
    when (type) {
        TransactionHistoryScreenState.TransactionType.PAY -> Res.drawable.ic_shopping_bag
        TransactionHistoryScreenState.TransactionType.SEND -> Res.drawable.ic_send
        TransactionHistoryScreenState.TransactionType.RECEIVE -> Res.drawable.ic_receive
    }

private fun getTransactionTitle(transactionType: TransactionHistoryScreenState.TransactionType): String =
    when (transactionType) {
        TransactionHistoryScreenState.TransactionType.PAY -> "Online shopping"
        TransactionHistoryScreenState.TransactionType.SEND -> "Send to"
        TransactionHistoryScreenState.TransactionType.RECEIVE -> "Receive from"
    }
