package net.thechance.mena.wallet.presentation.screen.transaction_history.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.wallet.presentation.screen.transaction_history.TransactionHistoryScreenState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun TransactionHistoryCard(
    transaction: TransactionHistoryScreenState.TransactionHistoryUiState,
    onTransactionCardClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onTransactionCardClicked() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TransactionStatusIcon(
            transactionType = transaction.type,
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

@OptIn(ExperimentalUuidApi::class)
@Preview(showBackground = true)
@Composable
private fun TransactionHistoryCardPreview() {
    val mockTransaction = TransactionHistoryScreenState.TransactionHistoryUiState(
        id = Uuid.parse("123e4567-e89b-12d3-a456-426614174000"),
        timeAndDate = "21 Oct 2025 - 10:00 AM",
        amount = "$150.00",
        type = TransactionHistoryScreenState.TransactionTypeUiState.ONLINE_SHOPPING,
        status = TransactionHistoryScreenState.TransactionStatusUiState.FAILED,
        contactName = "Ahmed Ali"
    )

    MenaTheme {
        TransactionHistoryCard(
            transaction = mockTransaction,
            onTransactionCardClicked = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}