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
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.transaction_type_icon
import mena.wallet_presentation.generated.resources.failed
import mena.wallet_presentation.generated.resources.ic_failed
import mena.wallet_presentation.generated.resources.ic_shopping_bag
import mena.wallet_presentation.generated.resources.img_silver
import mena.wallet_presentation.generated.resources.money_icon
import mena.wallet_presentation.generated.resources.transaction_pay
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.domain.entity.Transaction
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TransactionHistoryCard(
    transactionTypeIcon: DrawableResource,
    transactionTitle: StringResource,
    transactionTimeAndDate: String,
    amount: String,
    transactionStatus: Transaction.Status,
    onTransactionCardClicked: () -> Unit,
    modifier: Modifier = Modifier,
    sender: String? = null,
    receiver: String? = null
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .padding(vertical = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onTransactionCardClicked() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TransactionIcon(
            transactionTypeIcon = transactionTypeIcon,
            transactionStatus = transactionStatus
        )
        TransactionDataColumn(
            modifier = Modifier.weight(1f).wrapContentHeight().align(Alignment.CenterVertically),
            transactionTitle = transactionTitle,
            transactionTimeAndDate = transactionTimeAndDate,
            amount = amount,
            transactionStatus = transactionStatus,
            sender = sender,
            receiver = receiver,
        )
        Box(
            modifier = Modifier
                .weight(1f).height(1.dp)
                .background(Theme.colorScheme.stroke)
        )
    }
}

@Composable
private fun TransactionIcon(
    transactionTypeIcon: DrawableResource,
    transactionStatus: Transaction.Status,
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .background(Theme.colorScheme.primary.onPrimary, shape = CircleShape)
            .padding(12.dp)
    ) {
        Icon(
            painter = painterResource(transactionTypeIcon),
            contentDescription = Res.string.transaction_type_icon.toString(),
            modifier = Modifier.size(24.dp)
        )
        if (transactionStatus == Transaction.Status.FAIL) {
            Icon(
                painter = painterResource(Res.drawable.ic_failed),
                contentDescription = Res.string.failed.toString(),
                modifier = Modifier.align(Alignment.BottomCenter).offset(y = 18.dp)
            )
        }
    }
}

@Composable
private fun TransactionDataColumn(
    transactionTitle: StringResource,
    transactionTimeAndDate: String,
    amount: String,
    transactionStatus: Transaction.Status,
    modifier: Modifier = Modifier,
    sender: String? = null,
    receiver: String? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (transactionStatus == Transaction.Status.FAIL) Arrangement.spacedBy(
            4.dp
        ) else Arrangement.spacedBy(8.dp)
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
    }
}

@Composable
private fun TransactionTitleAndAmount(
    transactionTitle: StringResource,
    amount: String,
    sender: String?,
    receiver: String?,
    modifier: Modifier = Modifier
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
                text = transactionTitle.toString(),
                style = Theme.typography.body.small,
                color = Theme.colorScheme.shadePrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            sender?.let { name ->
                Text(
                    text = name,
                    style = Theme.typography.body.small,
                    color = Theme.colorScheme.shadePrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = SemiBold
                )
            }
            receiver?.let { name ->
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

@Composable
private fun TransactionAmountRow(
    amount: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            modifier = Modifier.padding(end = 4.dp),
            text = amount,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary
        )
        Icon(
            modifier = Modifier.size(16.dp),
            painter = painterResource(Res.drawable.img_silver),
            contentDescription = Res.string.money_icon.toString()
        )
    }
}

@Preview
@Composable
fun TransactionHistoryCardPreview() {
    TransactionHistoryCard(
        transactionTypeIcon = Res.drawable.ic_shopping_bag,
        transactionTitle = Res.string.transaction_pay,
        transactionTimeAndDate = "2025-09-27 14:45",
        amount = "120.55",
        transactionStatus = Transaction.Status.SUCCESS,
        onTransactionCardClicked = {},
        modifier = Modifier.fillMaxWidth()
    )
}
