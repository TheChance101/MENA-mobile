package net.thechance.mena.wallet.presentation.screen.payment_result.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.close
import mena.wallet_presentation.generated.resources.connection_lost_try_again
import mena.wallet_presentation.generated.resources.show_transaction_details
import mena.wallet_presentation.generated.resources.silvers
import mena.wallet_presentation.generated.resources.to
import mena.wallet_presentation.generated.resources.transaction_failed
import mena.wallet_presentation.generated.resources.transaction_success
import mena.wallet_presentation.generated.resources.transaction_successful
import mena.wallet_presentation.generated.resources.try_again
import mena.wallet_presentation.generated.resources.you_paid
import net.thechance.mena.designsystem.presentation.component.button.OutlinedButton
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.model.SubmissionStatus
import net.thechance.mena.wallet.presentation.screen.payment_result.PaymentResultInteractionListener
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun PaymentStatusBody(
    interactionListener: PaymentResultInteractionListener,
    paymentStatus: SubmissionStatus = SubmissionStatus.UNKNOWN_ERROR,
    description: String = stringResource(Res.string.connection_lost_try_again),
    receiverName: String = "",
    amount: Double = 0.0
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (paymentStatus == SubmissionStatus.UNKNOWN_ERROR) {
            PaymentResultCard(
                image = painterResource(Res.drawable.transaction_failed),
                title = stringResource(Res.string.transaction_failed),
                description = description,
                paymentStatus = paymentStatus,
                modifier = Modifier.align(Alignment.Center)
            )
            PaymentStatusButtons(
                primaryButtonText = stringResource(Res.string.try_again),
                onPrimaryButtonClick = interactionListener::onTryAgainClicked,
                onCancelClicked = interactionListener::onCancelClicked,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        } else {
            PaymentResultCard(
                image = painterResource(Res.drawable.transaction_success),
                title = stringResource(Res.string.transaction_successful),
                name = receiverName,
                amount = amount,
                paymentStatus = paymentStatus,
                modifier = Modifier.align(Alignment.Center)
            )
            PaymentStatusButtons(
                primaryButtonText = stringResource(Res.string.show_transaction_details),
                onPrimaryButtonClick = interactionListener::onShowTransactionDetailsClicked,
                onCancelClicked = interactionListener::onCancelClicked,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun PaymentResultCard(
    image: Painter,
    title: String,
    paymentStatus: SubmissionStatus,
    modifier: Modifier = Modifier,
    amount: Double = 0.0,
    description: String = "",
    name: String = ""
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 150.dp)
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.lg)
            )
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = image,
            contentDescription = title
        )
        Text(
            text = title,
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.title.small,
        )
        if (paymentStatus == SubmissionStatus.UNKNOWN_ERROR) {
            Text(
                text = description,
                color = Theme.colorScheme.shadeSecondary,
                style = Theme.typography.label.extraSmall,
                modifier = Modifier
                    .padding(top = 8.dp)
            )
        } else {
            SuccessPaymentDescription(
                name = name,
                amount = amount,
                modifier = Modifier
                    .padding(top = 8.dp)
            )
        }

    }
}

@Composable
private fun PaymentStatusButtons(
    primaryButtonText: String,
    onPrimaryButtonClick: () -> Unit,
    onCancelClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        PrimaryButton(
            text = primaryButtonText,
            onClick = onPrimaryButtonClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentPadding = PaddingValues(vertical = 13.dp)
        )

        OutlinedButton(
            text = stringResource(Res.string.close),
            onClick = onCancelClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 12.dp),
            contentPadding = PaddingValues(vertical = 13.dp),
        )
    }
}

@Composable
private fun SuccessPaymentDescription(
    name: String,
    amount: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = stringResource(Res.string.you_paid),
            color = Theme.colorScheme.shadeSecondary,
            style = Theme.typography.label.small
        )
        Text(
            text = "$amount ${stringResource(Res.string.silvers)}",
            color = Theme.colorScheme.shadeSecondary,
            style = Theme.typography.label.small.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = stringResource(Res.string.to),
            color = Theme.colorScheme.shadeSecondary,
            style = Theme.typography.label.small
        )
        Text(
            text = name,
            color = Theme.colorScheme.shadeSecondary,
            style = Theme.typography.label.small.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}