package net.thechance.mena.wallet.presentation.screen.transactiondetails.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.screen.transactiondetails.TransactionDetailsScreenState.TransactionDetailsUiState
import net.thechance.mena.wallet.presentation.screen.transactiondetails.TransactionDetailsScreenState.TransactionStatus
import net.thechance.mena.wallet.presentation.screen.transactiondetails.TransactionDetailsScreenState.TransactionType
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun DetailsSection(
    modifier: Modifier = Modifier,
    transactionDetailsUiState: TransactionDetailsUiState,
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
                TransactionType.PAY -> stringResource(Res.string.pay)
                TransactionType.SEND -> stringResource(Res.string.send)
                TransactionType.RECEIVE -> stringResource(Res.string.receive)
            },
            textStyle = Theme.typography.label.small,
            textColor = Theme.colorScheme.shadeSecondary,
            icon = when (transactionDetailsUiState.transactionType) {
                TransactionType.PAY -> painterResource(Res.drawable.ic_pay)
                TransactionType.SEND -> painterResource(Res.drawable.ic_send)
                TransactionType.RECEIVE -> painterResource(Res.drawable.ic_receive)
            },
            iconContentDescription = when (transactionDetailsUiState.transactionType) {
                TransactionType.PAY -> stringResource(Res.string.pay_button)
                TransactionType.SEND -> stringResource(Res.string.send_button)
                TransactionType.RECEIVE -> stringResource(Res.string.receive_button)
            },
            iconTint = Theme.colorScheme.shadeSecondary,
            iconSize = when (transactionDetailsUiState.transactionType) {
                TransactionType.PAY -> 16.dp
                TransactionType.SEND, TransactionType.RECEIVE -> 10.dp
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

        DetailsInfo(
            title = stringResource(Res.string.status),
            content = when (transactionDetailsUiState.transactionStatus) {
                TransactionStatus.FAILED -> stringResource(Res.string.failed)
                TransactionStatus.SUCCESS -> stringResource(Res.string.success)
            },
            icon = when (transactionDetailsUiState.transactionStatus) {
                TransactionStatus.FAILED -> painterResource(Res.drawable.ic_failed)
                TransactionStatus.SUCCESS -> painterResource(Res.drawable.ic_success)
            },
            iconContentDescription = when (transactionDetailsUiState.transactionStatus) {
                TransactionStatus.FAILED -> stringResource(Res.string.failed_icon)
                TransactionStatus.SUCCESS -> stringResource(Res.string.success_icon)
            },
            iconTint = when (transactionDetailsUiState.transactionStatus) {
                TransactionStatus.FAILED -> Theme.colorScheme.error
                TransactionStatus.SUCCESS -> Theme.colorScheme.success
            }
        )

        DetailsInfo(
            title = stringResource(Res.string.type),
            content = when (transactionDetailsUiState.transactionType) {
                TransactionType.SEND, TransactionType.RECEIVE -> stringResource(Res.string.transfer)
                TransactionType.PAY -> stringResource(Res.string.purchase)
            },
        )

        DetailsInfo(
            title = when (transactionDetailsUiState.transactionType) {
                TransactionType.SEND, TransactionType.PAY -> stringResource(Res.string.to)
                TransactionType.RECEIVE -> stringResource(Res.string.from)
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

@Preview
@Composable
private fun DetailsSectionPreview() {
    MenaTheme {
        DetailsSection(
            transactionDetailsUiState = TransactionDetailsUiState()
        )
    }
}