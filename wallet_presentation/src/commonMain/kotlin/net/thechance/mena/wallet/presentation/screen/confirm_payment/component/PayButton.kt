package net.thechance.mena.wallet.presentation.screen.confirm_payment.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.img_silver
import mena.wallet_presentation.generated.resources.pay_amount
import mena.wallet_presentation.generated.resources.silver_coin
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun PayButton(
    isLoading: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    payAmount: String,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        isLoading = isLoading,
        isEnabled = isEnabled,
        containerColor = Theme.colorScheme.primary.primary,
        disabledContainerColor = Theme.colorScheme.disabled,
        contentColor = Theme.colorScheme.primary.onPrimary,
        disabledContentColor = Theme.colorScheme.textDisabled,
        onClick = onClick,
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        shape = RoundedCornerShape(Theme.radius.md),
        loadingColors = listOf(
            Theme.colorScheme.primary.onPrimaryHint,
            Theme.colorScheme.primary.onPrimaryBody,
            Theme.colorScheme.primary.onPrimary
        ),
    ) {
        Text(
            text = stringResource(
                Res.string.pay_amount,
                payAmount
            ),
            style = Theme.typography.label.medium,
            color = it,
        )
        Icon(
            painter = painterResource(Res.drawable.img_silver),
            contentDescription = stringResource(Res.string.silver_coin),
            modifier = Modifier
                .padding(start = 8.dp)
                .size(20.dp),
        )
    }
}

@Preview
@Composable
private fun PayButtonPreview() {
    MenaTheme {
        PayButton(
            isLoading = false,
            isEnabled = true,
            onClick = {},
            payAmount = "5000"
        )
    }
}