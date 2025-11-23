package net.thechance.mena.dukan.presentation.screen.checkout.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.confirm_order
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

@Composable
fun ConfirmOrderButton(
    onConfirmOrderClicked: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                SquircleShape(
                    topStart = Theme.radius.xl,
                    topEnd = Theme.radius.xl
                )
            )
            .background(Theme.colorScheme.background.surfaceLow)
            .padding(Theme.spacing._16),
    ) {
        PrimaryButton(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            text = stringResource(Res.string.confirm_order),
            onClick = onConfirmOrderClicked,
            contentPadding = PaddingValues(vertical = Theme.spacing._12),
            isLoading = isLoading,
            isEnabled = !isLoading
        )
    }
}

@Preview
@Composable
private fun ConfirmOrderButtonPreview() {
    MenaTheme {
        ConfirmOrderButton({}, true)
    }
}
