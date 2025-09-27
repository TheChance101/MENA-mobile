package net.thechance.mena.wallet.presentation.screen.transactiondetails.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.date
import mena.wallet_presentation.generated.resources.failed
import mena.wallet_presentation.generated.resources.failed_icon
import mena.wallet_presentation.generated.resources.from
import mena.wallet_presentation.generated.resources.ic_failed
import mena.wallet_presentation.generated.resources.ic_pay
import mena.wallet_presentation.generated.resources.ic_receive
import mena.wallet_presentation.generated.resources.ic_send
import mena.wallet_presentation.generated.resources.ic_success
import mena.wallet_presentation.generated.resources.img_silver
import mena.wallet_presentation.generated.resources.pay
import mena.wallet_presentation.generated.resources.pay_button
import mena.wallet_presentation.generated.resources.purchase
import mena.wallet_presentation.generated.resources.receive
import mena.wallet_presentation.generated.resources.receive_button
import mena.wallet_presentation.generated.resources.send
import mena.wallet_presentation.generated.resources.send_button
import mena.wallet_presentation.generated.resources.silver_coin
import mena.wallet_presentation.generated.resources.status
import mena.wallet_presentation.generated.resources.success
import mena.wallet_presentation.generated.resources.success_icon
import mena.wallet_presentation.generated.resources.to
import mena.wallet_presentation.generated.resources.transaction_id
import mena.wallet_presentation.generated.resources.transfer
import mena.wallet_presentation.generated.resources.type
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.presentation.screen.transactiondetails.TransactionDetailsScreenState.TransactionDetailsUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun DetailsSection(
    modifier: Modifier = Modifier,
    transactionDetailsUiState: TransactionDetailsUiState,
    isUserNameShown: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(shape = RoundedCornerShape(16.dp))
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        TextWithIcon(
            modifier = Modifier.padding(top = 8.dp).align(Alignment.CenterHorizontally),
            text = when (transactionDetailsUiState.transactionType) {
                Transaction.Type.ONLINE_PURCHASE -> stringResource(Res.string.pay)
                Transaction.Type.SENT -> stringResource(Res.string.send)
                Transaction.Type.RECEIVED -> stringResource(Res.string.receive)
            },
            textStyle = Theme.typography.label.small,
            textColor = Theme.colorScheme.shadeSecondary,
            icon = when (transactionDetailsUiState.transactionType) {
                Transaction.Type.ONLINE_PURCHASE -> painterResource(Res.drawable.ic_pay)
                Transaction.Type.SENT -> painterResource(Res.drawable.ic_send)
                Transaction.Type.RECEIVED -> painterResource(Res.drawable.ic_receive)
            },
            iconContentDescription = when (transactionDetailsUiState.transactionType) {
                Transaction.Type.ONLINE_PURCHASE -> stringResource(Res.string.pay_button)
                Transaction.Type.SENT -> stringResource(Res.string.send_button)
                Transaction.Type.RECEIVED -> stringResource(Res.string.receive_button)
            },
            iconTint = Theme.colorScheme.shadeSecondary,
            iconSize = when (transactionDetailsUiState.transactionType) {
                Transaction.Type.ONLINE_PURCHASE -> 16.dp
                Transaction.Type.SENT, Transaction.Type.RECEIVED -> 10.dp
            },
            gap = 4.dp,
        )

        TextWithIcon(
            modifier = Modifier.padding(vertical = 8.dp).align(Alignment.CenterHorizontally),
            text = transactionDetailsUiState.amount,
            textStyle = Theme.typography.headline.medium,
            textColor = Theme.colorScheme.shadePrimary,
            icon = painterResource(Res.drawable.img_silver),
            iconContentDescription = stringResource(Res.string.silver_coin),
            iconSize = 24.dp,
            gap = 8.dp,
        )

        if (isUserNameShown){
            DetailsInfo(
                title = when (transactionDetailsUiState.transactionType) {
                    Transaction.Type.SENT, Transaction.Type.ONLINE_PURCHASE -> stringResource(Res.string.from)
                    Transaction.Type.RECEIVED -> stringResource(Res.string.to)
                },
                content = transactionDetailsUiState.userName,
            )
        }

        DetailsInfo(
            title = stringResource(Res.string.status),
            content = when (transactionDetailsUiState.transactionStatus) {
                Transaction.Status.FAIL -> stringResource(Res.string.failed)
                Transaction.Status.SUCCESS -> stringResource(Res.string.success)
            },
            icon = when (transactionDetailsUiState.transactionStatus) {
                Transaction.Status.FAIL -> painterResource(Res.drawable.ic_failed)
                Transaction.Status.SUCCESS -> painterResource(Res.drawable.ic_success)
            },
            iconContentDescription = when (transactionDetailsUiState.transactionStatus) {
                Transaction.Status.FAIL -> stringResource(Res.string.failed_icon)
                Transaction.Status.SUCCESS -> stringResource(Res.string.success_icon)
            },
            iconTint = when (transactionDetailsUiState.transactionStatus) {
                Transaction.Status.FAIL -> Theme.colorScheme.error
                Transaction.Status.SUCCESS -> Theme.colorScheme.success
            }
        )

        DetailsInfo(
            title = stringResource(Res.string.type),
            content = when (transactionDetailsUiState.transactionType) {
                Transaction.Type.SENT, Transaction.Type.RECEIVED -> stringResource(Res.string.transfer)
                Transaction.Type.ONLINE_PURCHASE -> stringResource(Res.string.purchase)
            },
        )

        DetailsInfo(
            title = when (transactionDetailsUiState.transactionType) {
                Transaction.Type.SENT, Transaction.Type.ONLINE_PURCHASE -> stringResource(Res.string.to)
                Transaction.Type.RECEIVED -> stringResource(Res.string.from)
            },
            content = transactionDetailsUiState.otherParty,
        )

        DetailsInfo(
            title = stringResource(Res.string.date),
            content = transactionDetailsUiState.date,
        )

        DetailsInfo(
            title = stringResource(Res.string.transaction_id),
            content = transactionDetailsUiState.id,
        )
    }
}

@Composable
private fun TextWithIcon(
    text: String,
    textStyle: TextStyle,
    textColor: Color,
    icon: Painter,
    iconContentDescription: String,
    iconTint: Color = Color.Unspecified,
    iconSize: Dp,
    gap: Dp = 4.dp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = textStyle,
            color = textColor
        )
        Icon(
            painter = icon,
            contentDescription = iconContentDescription,
            modifier = Modifier.padding(start = gap).size(iconSize),
            tint = iconTint
        )
    }
}


@Composable
private fun ColumnScope.DetailsInfo(
    title: String,
    content: String,
    icon: Painter? = null,
    iconContentDescription: String = "",
    iconTint: Color = Theme.colorScheme.success
) {
    Box(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .height(1.dp)
            .fillMaxWidth()
            .background(color = Theme.colorScheme.stroke)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary,
            maxLines = 1
        )
        Row {
            icon?.let {
                Icon(
                    painter = icon,
                    contentDescription = iconContentDescription,
                    modifier = Modifier.padding(end = 4.dp).size(20.dp),
                    tint = iconTint
                )
            }
            Text(
                text = content,
                style = Theme.typography.label.medium,
                overflow = TextOverflow.Ellipsis,
                color = Theme.colorScheme.shadePrimary,
                maxLines = 1,
                textAlign = TextAlign.End
            )
        }
    }
}

@Preview
@Composable
private fun DetailsSectionPreview() {
    MenaTheme {
        DetailsSection(
            transactionDetailsUiState = TransactionDetailsUiState()
        )
    }
}