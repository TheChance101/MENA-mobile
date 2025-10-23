package net.thechance.mena.wallet.presentation.screen.payment_result.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.close
import mena.wallet_presentation.generated.resources.try_again
import net.thechance.mena.designsystem.presentation.component.button.OutlinedButton
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun PaymentActionButtons(
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
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 13.dp),
            isEnabled = isCloseEnabled
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun PaymentActionButtonsPreview() {
    MenaTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            PaymentActionButtons(
                primaryButtonText = stringResource(Res.string.try_again),
                onPrimaryButtonClick = {},
                onCancelClicked = {},
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}