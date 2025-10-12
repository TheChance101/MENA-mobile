package net.thechance.mena.wallet.presentation.screen.payment_result.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.model.SubmitTransactionResultStatus

@Composable
fun PaymentResultCard(
    image: Painter,
    title: String,
    paymentStatus: SubmitTransactionResultStatus,
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
        if (paymentStatus == SubmitTransactionResultStatus.UNKNOWN_ERORR) {
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
