package net.thechance.mena.wallet.presentation.screen.payment_result.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
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
import mena.wallet_presentation.generated.resources.payment_failed_description
import mena.wallet_presentation.generated.resources.payment_status_crossfade
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
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.model.SubmissionStatus
import net.thechance.mena.wallet.presentation.screen.payment_result.PaymentResultInteractionListener
import net.thechance.mena.wallet.presentation.screen.payment_result.PaymentResultScreenState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PaymentStatusBody(
    interactionListener: PaymentResultInteractionListener,
    status: PaymentResultScreenState,
    paymentStatus: SubmissionStatus = SubmissionStatus.CONNECTION_LOST,
    receiverName: String = "",
    amount: Double = 0.0
) {
    Crossfade(
        targetState = paymentStatus,
        animationSpec = tween(durationMillis = 300),
        label = stringResource(Res.string.payment_status_crossfade)
    ) { state ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (state) {
                SubmissionStatus.CONNECTION_LOST -> {
                    PaymentResultCard(
                        image = painterResource(Res.drawable.transaction_failed),
                        title = stringResource(Res.string.transaction_failed),
                        description = stringResource(Res.string.connection_lost_try_again),
                        paymentStatus = state,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    PaymentStatusButtons(
                        primaryButtonText = stringResource(Res.string.try_again),
                        onPrimaryButtonClick = interactionListener::onTryAgainClicked,
                        onCancelClicked = interactionListener::onCloseClicked,
                        modifier = Modifier.align(Alignment.BottomCenter),
                        isLoading = status.isLoading,
                        isCloseEnabled = status.isCloseButtonEnabled,
                        isTryAgainEnabled = status.isTryAgainButtonEnabled
                    )
                }

                SubmissionStatus.UNKNOWN_ERROR -> {
                    PaymentResultCard(
                        image = painterResource(Res.drawable.transaction_failed),
                        title = stringResource(Res.string.transaction_failed),
                        description = stringResource(Res.string.payment_failed_description),
                        paymentStatus = state,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    PaymentStatusButtons(
                        primaryButtonText = stringResource(Res.string.try_again),
                        onPrimaryButtonClick = interactionListener::onTryAgainClicked,
                        onCancelClicked = interactionListener::onCloseClicked,
                        modifier = Modifier.align(Alignment.BottomCenter),
                        isLoading = status.isLoading,
                        isCloseEnabled = status.isCloseButtonEnabled,
                        isTryAgainEnabled = status.isTryAgainButtonEnabled
                    )
                }

                SubmissionStatus.SUCCESS -> {
                    PaymentResultCard(
                        image = painterResource(Res.drawable.transaction_success),
                        title = stringResource(Res.string.transaction_successful),
                        name = receiverName,
                        amount = amount,
                        paymentStatus = state,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    PaymentStatusButtons(
                        primaryButtonText = stringResource(Res.string.show_transaction_details),
                        onPrimaryButtonClick = interactionListener::onShowTransactionDetailsClicked,
                        onCancelClicked = interactionListener::onCloseClicked,
                        modifier = Modifier.align(Alignment.BottomCenter),
                        isLoading = status.isLoading,
                        isCloseEnabled = status.isCloseButtonEnabled
                    )
                }
            }
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
        if (paymentStatus == SubmissionStatus.SUCCESS) {
            SuccessPaymentDescription(
                name = name,
                amount = amount,
                modifier = Modifier
                    .padding(top = 8.dp)
            )
        } else {
            Text(
                text = description,
                color = Theme.colorScheme.shadeSecondary,
                style = Theme.typography.label.extraSmall,
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
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isCloseEnabled: Boolean = true,
    isTryAgainEnabled: Boolean = true
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
                .padding(bottom = 12.dp),
            contentPadding = PaddingValues(vertical = 13.dp),
            isLoading = isLoading,
            isEnabled = isTryAgainEnabled
        )

        OutlinedButton(
            text = stringResource(Res.string.close),
            onClick = onCancelClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            contentPadding = PaddingValues(vertical = 13.dp),
            isEnabled = isCloseEnabled
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
            text = " $amount ${stringResource(Res.string.silvers)} ",
            color = Theme.colorScheme.shadeSecondary,
            style = Theme.typography.label.small.copy(
                fontWeight = FontWeight.ExtraBold
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
                fontWeight = FontWeight.ExtraBold
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentResultSuccessPreview() {
    MenaTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            PaymentResultCard(
                image = painterResource(Res.drawable.transaction_success),
                title = stringResource(Res.string.transaction_successful),
                paymentStatus = SubmissionStatus.SUCCESS,
                name = "Ahmed Ali",
                amount = 31.99,
                modifier = Modifier.align(Alignment.Center)
            )
            PaymentStatusButtons(
                primaryButtonText = stringResource(Res.string.try_again),
                onPrimaryButtonClick = {},
                onCancelClicked = {},
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentResultUnknownErrorPreview() {
    MenaTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            PaymentResultCard(
                image = painterResource(Res.drawable.transaction_failed),
                title = stringResource(Res.string.transaction_failed),
                paymentStatus = SubmissionStatus.UNKNOWN_ERROR,
                description = stringResource(Res.string.payment_failed_description),
                modifier = Modifier.align(Alignment.Center)
            )
            PaymentStatusButtons(
                primaryButtonText = stringResource(Res.string.try_again),
                onPrimaryButtonClick = {},
                onCancelClicked = {},
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}