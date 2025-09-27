package net.thechance.mena.wallet.presentation.screen.transaction_history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.back_button
import mena.wallet_presentation.generated.resources.filter
import mena.wallet_presentation.generated.resources.ic_arrow_left
import mena.wallet_presentation.generated.resources.ic_filter
import mena.wallet_presentation.generated.resources.ic_receive
import mena.wallet_presentation.generated.resources.ic_send
import mena.wallet_presentation.generated.resources.ic_share
import mena.wallet_presentation.generated.resources.ic_shopping_bag
import mena.wallet_presentation.generated.resources.share
import mena.wallet_presentation.generated.resources.transaction_pay
import mena.wallet_presentation.generated.resources.transaction_receive
import mena.wallet_presentation.generated.resources.transaction_send
import mena.wallet_presentation.generated.resources.transactions_history
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.presentation.component.WalletScaffold
import net.thechance.mena.wallet.presentation.screen.transaction_history.component.TransactionHistoryCard
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
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
                title = Res.string.transactions_history.toString(),
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
                        modifier = Modifier.clickable { interactionListener.onShareClicked() },
                        painter = painterResource(Res.drawable.ic_share),
                        contentDescription = Res.string.share.toString()
                    )
                },
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colorScheme.background.surface)
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            item {
                Button(
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                    onClick = interactionListener::onFilterClicked,
                    containerColor = Theme.colorScheme.brand.brandVariant,
                    shape = CircleShape,
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_filter),
                        contentDescription = Res.string.filter.toString()
                    )
                    Text(
                        modifier = Modifier.padding(start = 4.dp),
                        text = Res.string.filter.toString(),
                        style = Theme.typography.label.small,
                        color = Theme.colorScheme.primary.primary
                    )
                }
            }
            items(state) { transaction ->
                TransactionHistoryCard(
                    transactionTypeIcon = getTransactionTypeIcon(transaction.type),
                    transactionTitle = getTransactionTitle(transaction.type),
                    transactionTimeAndDate = transaction.timeAndDate,
                    amount = transaction.amount,
                    transactionStatus = transaction.status,
                    onTransactionCardClicked = interactionListener::onTransactionCardClicked,
                    sender = transaction.sender,
                    receiver = transaction.receiver
                )

            }
        }
    }
}

private fun getTransactionTypeIcon(type: Transaction.Type): DrawableResource =
    when (type) {
        Transaction.Type.ONLINE_PURCHASE -> Res.drawable.ic_shopping_bag
        Transaction.Type.SENT -> Res.drawable.ic_send
        Transaction.Type.RECEIVED -> Res.drawable.ic_receive
    }

private fun getTransactionTitle(transactionType: Transaction.Type): StringResource =
    when (transactionType) {
        Transaction.Type.ONLINE_PURCHASE -> Res.string.transaction_pay
        Transaction.Type.SENT -> Res.string.transaction_send
        Transaction.Type.RECEIVED -> Res.string.transaction_receive
    }