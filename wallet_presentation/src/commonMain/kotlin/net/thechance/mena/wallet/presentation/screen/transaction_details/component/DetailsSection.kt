package net.thechance.mena.wallet.presentation.screen.transaction_details.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.date
import mena.wallet_presentation.generated.resources.img_silver
import mena.wallet_presentation.generated.resources.silver_coin
import mena.wallet_presentation.generated.resources.status
import mena.wallet_presentation.generated.resources.transaction_id
import mena.wallet_presentation.generated.resources.type
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.screen.transaction_details.TransactionDetailsScreenState.TransactionDetailsUiState
import net.thechance.mena.wallet.presentation.screen.transaction_details.TransactionDetailsScreenState.TransactionStatusUiState
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
            .padding(horizontal = 16.dp)
            .clip(shape = RoundedCornerShape(16.dp))
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        HeaderSection(transactionDetailsUiState)

        if (isUserNameShown) {
            DetailsInfo(
                title = stringResource(transactionDetailsUiState.userInfo),
                content = transactionDetailsUiState.userName,
            )
        }

        DetailsInfo(
            title = stringResource(Res.string.status),
            content = stringResource(transactionDetailsUiState.transactionStatus.contentRes),
            icon = painterResource(transactionDetailsUiState.transactionStatus.iconRes),
            iconContentDescription = stringResource(transactionDetailsUiState.transactionStatus.iconContentDescriptionRes),
            iconTint = transactionDetailsUiState.transactionStatus.getStatusColor()
        )

        DetailsInfoSection(transactionDetailsUiState)
    }
}

@Composable
private fun ColumnScope.HeaderSection(transactionDetailsUiState: TransactionDetailsUiState) {
    TextWithIcon(
        modifier = Modifier
            .padding(top = 8.dp)
            .align(Alignment.CenterHorizontally),
        text = stringResource(transactionDetailsUiState.transactionType.titleRes),
        textStyle = Theme.typography.label.small,
        textColor = Theme.colorScheme.shadeSecondary,
        icon = painterResource(transactionDetailsUiState.transactionType.iconRes),
        iconContentDescription = stringResource(transactionDetailsUiState.transactionType.iconContentDescriptionRes),
        iconTint = transactionDetailsUiState.transactionType.iconTint(),
        iconSize = 16.dp,
        gap = 4.dp,
    )

    TextWithIcon(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .align(Alignment.CenterHorizontally),
        text = transactionDetailsUiState.amount,
        textStyle = Theme.typography.headline.medium,
        textColor = Theme.colorScheme.shadePrimary,
        icon = painterResource(Res.drawable.img_silver),
        iconContentDescription = stringResource(Res.string.silver_coin),
        iconSize = 24.dp,
        gap = 8.dp,
    )
}

@Composable
private fun ColumnScope.DetailsInfoSection(transactionDetailsUiState: TransactionDetailsUiState) {
    DetailsInfo(
        title = stringResource(Res.string.type),
        content = stringResource(transactionDetailsUiState.typeContent),
    )

    DetailsInfo(
        title = stringResource(transactionDetailsUiState.otherPartyTitle),
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

@Composable
private fun TransactionStatusUiState.getStatusColor(): Color = when (this) {
    TransactionStatusUiState.FAILED -> Theme.colorScheme.error
    TransactionStatusUiState.SUCCESS -> Theme.colorScheme.success
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