package net.thechance.mena.wallet.presentation.screen.transaction_history.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.ic_failed
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.screen.transaction_history.TransactionHistoryScreenState
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun TransactionHistoryCard(
    transactionTypeIcon: DrawableResource,
    transactionTitle: String,
    transactionTimeAndDate: String,
    amount: String,
    transactionStatus: TransactionHistoryScreenState.TransactionStatus,
    sender: String? = null,
    receiver: String? = null,
    onTransactionCardClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().wrapContentHeight().padding(vertical = 16.dp)
            .clickable { onTransactionCardClicked() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.wrapContentSize()
                .background(Theme.colorScheme.primary.onPrimary, shape = CircleShape).padding(12.dp)
        ) {
            Icon(
                painter = painterResource(transactionTypeIcon),
                contentDescription = "transaction type icon"
            )
            if (transactionStatus == TransactionHistoryScreenState.TransactionStatus.FAILED) {
                Icon(
                    painter = painterResource(Res.drawable.ic_failed),
                    contentDescription = "failed icon",
                    modifier = Modifier.offset(y = (-8).dp)
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f).wrapContentHeight().align(Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row {
                TransactionTitleAndAmount(
                    transactionTitle = transactionTitle,
                    amount = amount
                )
                if (sender != null) {
                    Text(
                        text = sender
                    )
                }
                if (receiver != null) {
                    Text(
                        text = receiver
                    )
                }
            }
            if (transactionStatus == TransactionHistoryScreenState.TransactionStatus.FAILED) {
                Text(
                    text = "Failed",
                    style = Theme.typography.label.small,
                    color = Theme.colorScheme.error
                )
            }
            Text(
                text = transactionTimeAndDate,
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadeSecondary
            )
        }
    }
}
