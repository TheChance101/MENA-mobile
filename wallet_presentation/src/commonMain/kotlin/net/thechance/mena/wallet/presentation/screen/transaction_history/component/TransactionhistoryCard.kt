package net.thechance.mena.wallet.presentation.screen.transaction_history.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import net.thechance.mena.wallet.presentation.screen.transaction_history.TransactionHistoryScreenState
import org.jetbrains.compose.resources.stringResource

@Composable
fun TransactionHistoryCard(
    transaction: TransactionHistoryScreenState.TransactionHistoryUiState,
    onTransactionCardClicked: () -> Unit,
    modifier: Modifier = Modifier
    ) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onTransactionCardClicked() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TransactionStatusIcon(
            transactionTypeIcon = transaction.type.iconRes,
            transactionStatus = transaction.status
        )
        TransactionContent(
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight()
                .align(Alignment.CenterVertically),
            transactionTitle = stringResource(transaction.type.titleRes),
            transactionTimeAndDate = transaction.timeAndDate,
            amount = transaction.amount,
            transactionStatus = transaction.status,
            contactName = transaction.contactName
        )
    }
}