package net.thechance.mena.wallet.presentation.screen.confirm_payment.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.You_are_about_to_pay
import mena.wallet_presentation.generated.resources.ic_user
import mena.wallet_presentation.generated.resources.img_silver
import mena.wallet_presentation.generated.resources.payment_to
import mena.wallet_presentation.generated.resources.silver_coin
import mena.wallet_presentation.generated.resources.user_img
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.screen.confirm_payment.ConfirmPaymentScreenState.PaymentUiState
import net.thechance.mena.wallet.presentation.screen.confirm_payment.ConfirmPaymentScreenState.ReceiverUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun PaymentDetailsSection(
    payment: PaymentUiState,
    receiver: ReceiverUiState,
    userMessage: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        PaymentInfoSection(
            amount = payment.amount,
            receiverName = receiver.name,
            receiverImage = receiver.profileImg
        )

        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            text = userMessage,
            style = Theme.typography.body.small,
            color = if (payment.status) Theme.colorScheme.shadeSecondary else Theme.colorScheme.error,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PaymentInfoSection(
    amount: String,
    receiverName: String,
    receiverImage: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(16.dp))
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(24.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.You_are_about_to_pay),
            style = Theme.typography.label.extraSmall,
            color = Theme.colorScheme.shadeSecondary,
            textAlign = TextAlign.Center
        )

        PaymentAmount(amount = amount)

        ReceiverInfo(receiverName = receiverName, receiverImage = receiverImage)
    }
}

@Composable
private fun PaymentAmount(
    amount: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = amount,
            style = Theme.typography.headline.medium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = Theme.colorScheme.shadePrimary
        )
        Icon(
            modifier = Modifier
                .padding(start = 8.dp)
                .size(24.dp),
            painter = painterResource(Res.drawable.img_silver),
            contentDescription = stringResource(Res.string.silver_coin),
        )
    }
}

@Composable
private fun ReceiverInfo(
    modifier: Modifier = Modifier,
    receiverName: String,
    receiverImage: String?,
) {
    Row(
        modifier = modifier
            .padding(top = 24.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(Res.string.payment_to),
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary
        )
        if (receiverImage == null) {
            Icon(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(20.dp)
                    .background(color = Theme.colorScheme.stroke, shape = CircleShape)
                    .padding(2.dp)
                    .clip(CircleShape),
                painter = painterResource(Res.drawable.ic_user),
                contentDescription = stringResource(Res.string.user_img),
                tint = Theme.colorScheme.shadeTertiary
            )
        } else {
            AsyncImage(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clip(CircleShape)
                    .size(20.dp),
                model = receiverImage,
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(Res.string.user_img),
            )
        }
        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = receiverName,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary
        )
    }
}

@Preview
@Composable
private fun PaymentDetailsSectionPreview() {
    MenaTheme {
        Scaffold {
            PaymentDetailsSection(
                payment = PaymentUiState(
                    amount = "530,320",
                    status = true
                ),
                userMessage = "You have 1,230.25 silvers in wallet",
                receiver = ReceiverUiState(
                    name = "Ahmed Ali"
                )
            )
        }
    }
}