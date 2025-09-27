package net.thechance.mena.wallet.presentation.screen.transaction_history.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.img_silver
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun TransactionTitleAndAmount(
    modifier: Modifier = Modifier,
    transactionTitle: StringResource,
    amount: String,
    sender: String?,
    receiver: String?
) {
    Row(
        modifier = modifier.fillMaxWidth().wrapContentHeight(),
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
            painter = painterResource(Res.drawable.img_silver),
            contentDescription = "money icon",
            Modifier.size(16.dp)
        )
    }
}