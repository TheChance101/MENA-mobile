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
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.ic_shopping_bag
import mena.wallet_presentation.generated.resources.transaction_pay
import net.thechance.mena.wallet.domain.entity.Transaction
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TransactionHistoryCard(
    transactionTypeIcon: DrawableResource,
    transactionTitle: String,
    transactionTimeAndDate: String,
    amount: String,
    transactionStatus: Transaction.Status,
    onTransactionCardClicked: () -> Unit,
    modifier: Modifier = Modifier,
    contactName: String? = null
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
            transactionTypeIcon = transactionTypeIcon,
            transactionStatus = transactionStatus
        )
        TransactionContent(
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight()
                .align(Alignment.CenterVertically),
            transactionTitle = transactionTitle,
            transactionTimeAndDate = transactionTimeAndDate,
            amount = amount,
            transactionStatus = transactionStatus,
            contactName = contactName
        )
    }
}

@Preview
@Composable
fun TransactionHistoryCardPreview() {
    TransactionHistoryCard(
        transactionTypeIcon = Res.drawable.ic_shopping_bag,
        transactionTitle = stringResource(Res.string.transaction_pay),
        transactionTimeAndDate = "2025-09-27 14:45",
        amount = "120.55",
        transactionStatus = Transaction.Status.SUCCESS,
        onTransactionCardClicked = {},
        modifier = Modifier.fillMaxWidth()
    )
}
