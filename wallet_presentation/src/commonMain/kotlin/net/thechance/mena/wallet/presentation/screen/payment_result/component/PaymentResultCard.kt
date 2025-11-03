package net.thechance.mena.wallet.presentation.screen.payment_result.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import mena.wallet_presentation.generated.resources.payment_failed_description
import mena.wallet_presentation.generated.resources.silvers
import mena.wallet_presentation.generated.resources.to
import mena.wallet_presentation.generated.resources.transaction_failed
import mena.wallet_presentation.generated.resources.transaction_success
import mena.wallet_presentation.generated.resources.transaction_successful
import mena.wallet_presentation.generated.resources.you_paid
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.model.SubmissionStatus
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun PaymentResultCard(
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
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.lg)
            )
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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
private fun SuccessPaymentDescription(
    name: String,
    amount: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.you_paid),
            color = Theme.colorScheme.shadeSecondary,
            style = Theme.typography.label.extraSmall
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
            style = Theme.typography.label.extraSmall
        )
        Text(
            text = " $name",
            color = Theme.colorScheme.shadeSecondary,
            style = Theme.typography.label.small.copy(
                fontWeight = FontWeight.ExtraBold
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentActionButtonsPreview() {
    MenaTheme {
        PaymentResultCard(
            image = painterResource(Res.drawable.transaction_failed),
            title = stringResource(Res.string.transaction_failed),
            paymentStatus = SubmissionStatus.UNKNOWN_ERROR,
            description = stringResource(Res.string.payment_failed_description),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SuccessPaymentPreview() {
    MenaTheme {
        PaymentResultCard(
            image = painterResource(Res.drawable.transaction_success),
            title = stringResource(Res.string.transaction_successful),
            paymentStatus = SubmissionStatus.SUCCESS,
            name = "Ahmed Ali",
            amount = 31.99
        )
    }
}