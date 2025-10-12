package net.thechance.mena.wallet.presentation.screen.payment_result.component

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.silvers
import mena.wallet_presentation.generated.resources.to
import mena.wallet_presentation.generated.resources.you_paid
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource

@Composable
fun SuccessPaymentDescription(
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