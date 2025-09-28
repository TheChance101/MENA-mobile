package net.thechance.mena.wallet.presentation.screen.transaction_details.component

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
import mena.wallet_presentation.generated.resources.img_silver
import mena.wallet_presentation.generated.resources.silver_coin
import mena.wallet_presentation.generated.resources.status
import mena.wallet_presentation.generated.resources.transaction_id
import mena.wallet_presentation.generated.resources.type
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.presentation.screen.transaction_details.TransactionDetailsScreenState.TransactionDetailsUiState
import net.thechance.mena.wallet.presentation.screen.transaction_details.getIconTint
import net.thechance.mena.wallet.presentation.screen.transaction_details.getOtherPartyTitle
import net.thechance.mena.wallet.presentation.screen.transaction_details.getStatusContent
import net.thechance.mena.wallet.presentation.screen.transaction_details.getStatusIcon
import net.thechance.mena.wallet.presentation.screen.transaction_details.getStatusIconDescription
import net.thechance.mena.wallet.presentation.screen.transaction_details.getTransactionTypeIcon
import net.thechance.mena.wallet.presentation.screen.transaction_details.getTransactionTypeIconDescription
import net.thechance.mena.wallet.presentation.screen.transaction_details.getTransactionTypeText
import net.thechance.mena.wallet.presentation.screen.transaction_details.getTypeContent
import net.thechance.mena.wallet.presentation.screen.transaction_details.getUserInfo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun DetailsSection(
    transactionDetailsUiState: TransactionDetailsUiState,
    modifier: Modifier = Modifier,
    isUserNameShown: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing._16)
            .clip(shape = RoundedCornerShape(Theme.spacing._16))
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.spacing._16)
            )
            .padding(Theme.spacing._16)
    ) {
        TextWithIcon(
            modifier = Modifier
                .padding(top = Theme.spacing._8)
                .align(Alignment.CenterHorizontally),
            text = stringResource(
                resource = getTransactionTypeText(transactionDetailsUiState.transactionType)
            ),
            textStyle = Theme.typography.label.small,
            textColor = Theme.colorScheme.shadeSecondary,
            icon = painterResource(
                resource = getTransactionTypeIcon(transactionDetailsUiState.transactionType)
            ),
            iconContentDescription = stringResource(
                resource = getTransactionTypeIconDescription(transactionDetailsUiState.transactionType)
            ),
            iconTint = Theme.colorScheme.shadeSecondary,
            iconSize = when (transactionDetailsUiState.transactionType) {
                Transaction.Type.ONLINE_PURCHASE -> Theme.spacing._16
                Transaction.Type.SENT, Transaction.Type.RECEIVED -> 10.dp
            },
            gap = Theme.spacing._4,
        )

        TextWithIcon(
            modifier = Modifier
                .padding(vertical = Theme.spacing._8)
                .align(Alignment.CenterHorizontally),
            text = transactionDetailsUiState.amount,
            textStyle = Theme.typography.headline.medium,
            textColor = Theme.colorScheme.shadePrimary,
            icon = painterResource(Res.drawable.img_silver),
            iconContentDescription = stringResource(Res.string.silver_coin),
            iconSize = Theme.spacing._24,
            gap = Theme.spacing._8,
        )

        if (isUserNameShown) {
            DetailsInfo(
                title = stringResource(
                    resource = getUserInfo(transactionDetailsUiState.transactionType)
                ),
                content = transactionDetailsUiState.userName,
            )
        }

        DetailsInfo(
            title = stringResource(Res.string.status),
            content = stringResource(
                resource = getStatusContent(transactionDetailsUiState.transactionStatus)
            ),
            icon = painterResource(
                resource = getStatusIcon(transactionDetailsUiState.transactionStatus)
            ),
            iconContentDescription = stringResource(
                resource = getStatusIconDescription (transactionDetailsUiState.transactionStatus)
            ),
            iconTint = getIconTint(transactionDetailsUiState.transactionStatus)
        )

        DetailsInfo(
            title = stringResource(Res.string.type),
            content =  stringResource(
                resource = getTypeContent(transactionDetailsUiState.transactionType)
            ),
        )

        DetailsInfo(
            title = stringResource(
                resource = getOtherPartyTitle (transactionDetailsUiState.transactionType)
            ),
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
    iconSize: Dp,
    modifier: Modifier = Modifier,
    iconTint: Color = Color.Unspecified,
    gap: Dp = Theme.spacing._4,
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
            modifier = Modifier
                .padding(start = gap)
                .size(iconSize),
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
            .padding(vertical = Theme.spacing._12)
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
        DetailsInfoContent(
            content = content,
            icon = icon,
            iconContentDescription = iconContentDescription,
            iconTint = iconTint
        )
    }
}

@Composable
private fun DetailsInfoContent(
    content: String,
    icon: Painter? = null,
    iconContentDescription: String = "",
    iconTint: Color = Theme.colorScheme.success
){
    Row {
        icon?.let {
            Icon(
                painter = icon,
                contentDescription = iconContentDescription,
                modifier = Modifier
                    .padding(end = Theme.spacing._4)
                    .size(20.dp),
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

@Preview
@Composable
private fun DetailsSectionPreview() {
    MenaTheme {
        DetailsSection(
            transactionDetailsUiState = TransactionDetailsUiState()
        )
    }
}