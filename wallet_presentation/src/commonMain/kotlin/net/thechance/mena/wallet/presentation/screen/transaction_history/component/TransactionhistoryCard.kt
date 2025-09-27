package net.thechance.mena.wallet.presentation.screen.transaction_history.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.error
import mena.wallet_presentation.generated.resources.failed
import mena.wallet_presentation.generated.resources.ic_failed
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.domain.entity.Transaction
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun TransactionHistoryCard(
    transactionTypeIcon: DrawableResource,
    transactionTitle: StringResource,
    transactionTimeAndDate: String,
    amount: String,
    transactionStatus: Transaction.Status,
    sender: String? = null,
    receiver: String? = null,
    onTransactionCardClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .padding(vertical = 16.dp)
            .clip(RoundedCornerShape(12.dp))
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
                contentDescription = "transaction type icon",
                modifier = Modifier.size(24.dp)
            )
            if (transactionStatus == Transaction.Status.FAIL) {
                Icon(
                    painter = painterResource(Res.drawable.ic_failed),
                    contentDescription = Res.string.error.toString(),
                    modifier = Modifier.align(Alignment.BottomCenter).offset(y = 20.dp)
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f).wrapContentHeight().align(Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (transactionStatus == Transaction.Status.FAIL) Arrangement.spacedBy(4.dp) else Arrangement.spacedBy(8.dp)
        ) {
            TransactionTitleAndAmount(
                transactionTitle = transactionTitle,
                amount = amount,
                sender = sender,
                receiver = receiver
            )
            if (transactionStatus == Transaction.Status.FAIL) {
                Text(
                    modifier = Modifier.align(Alignment.Start),
                    text = Res.string.failed.toString(),
                    style = Theme.typography.label.small,
                    color = Theme.colorScheme.error
                )
            }
            Text(
                modifier = Modifier.align(Alignment.Start),
                text = transactionTimeAndDate,
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadeSecondary
            )
            Box(
                modifier =Modifier
                    .fillMaxWidth().height(1.dp)
                    .background(Theme.colorScheme.stroke)
            )
        }
    }
}
