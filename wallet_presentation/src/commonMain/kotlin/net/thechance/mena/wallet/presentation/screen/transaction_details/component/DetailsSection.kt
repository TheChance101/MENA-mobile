package net.thechance.mena.wallet.presentation.screen.transaction_details.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import net.thechance.mena.wallet.presentation.screen.transaction_details.getIconSize
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
            iconSize = getIconSize(transactionDetailsUiState.transactionType),
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

@Preview
@Composable
private fun DetailsSectionPreview() {
    MenaTheme {
        DetailsSection(
            transactionDetailsUiState = TransactionDetailsUiState()
        )
    }
}