package net.thechance.mena.wallet.presentation.screen.transaction_history.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.failed
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.screen.transaction_history.TransactionHistoryScreenState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TransactionContent(
    transactionTitle: String,
    transactionTimeAndDate: String,
    amount: String,
    transactionStatus: TransactionHistoryScreenState.TransactionStatusUiState,
    modifier: Modifier = Modifier,
    contactName: String? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (transactionStatus.contentRes == Res.string.failed) Arrangement.spacedBy(
            4.dp
        ) else Arrangement.spacedBy(8.dp)
    ) {
        TransactionTitleAndAmount(
            transactionTitle = transactionTitle,
            amount = amount,
            contactName = contactName
        )
        if (transactionStatus.contentRes == Res.string.failed) {
            Text(
                modifier = Modifier.align(Alignment.Start),
                text = stringResource(transactionStatus.contentRes),
                style = Theme.typography.label.small,
                color = Theme.colorScheme.error
            )
        }
        Text(
            modifier = Modifier.align(Alignment.Start),
            text = transactionTimeAndDate,
            style = Theme.typography.label.extraSmall,
            color = Theme.colorScheme.shadeSecondary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionContentWithContactPreview() {
    MenaTheme {
        TransactionContent(
            transactionTitle = "Send to ",
            transactionTimeAndDate = "Oct 15, 2024, 02:20 PM",
            amount = "1,000.00",
            transactionStatus = TransactionHistoryScreenState.TransactionStatusUiState.SUCCESS,
            contactName = "Ahmed Mohamed"
        )
    }
}
