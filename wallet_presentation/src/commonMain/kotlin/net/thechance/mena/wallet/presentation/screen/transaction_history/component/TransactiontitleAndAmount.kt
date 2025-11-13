package net.thechance.mena.wallet.presentation.screen.transaction_history.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TransactionTitleAndAmount(
    transactionTitle: String,
    amount: String,
    modifier: Modifier = Modifier,
    contactName: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f, fill = false),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$transactionTitle ",
                style = Theme.typography.body.small,
                color = Theme.colorScheme.shadePrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            contactName?.let { name ->
                Text(
                    text = name,
                    style = Theme.typography.body.small,
                    color = Theme.colorScheme.shadePrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = SemiBold
                )
            }
        }

        TransactionAmountRow(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp),
            amount = amount
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionTitleAndAmountPreview() {
    MenaTheme {
        TransactionTitleAndAmount(
            transactionTitle = "Send Money",
            amount = "500.00"
        )
    }
}
