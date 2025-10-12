package net.thechance.mena.wallet.presentation.screen.payment_result.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.close
import net.thechance.mena.designsystem.presentation.component.button.OutlinedButton
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import org.jetbrains.compose.resources.stringResource

@Composable
fun PaymentStatusButtons(
    primaryButtonText: String,
    onPrimaryButtonClick: () -> Unit,
    onCancelClicked: () -> Unit,
    modifier: Modifier
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