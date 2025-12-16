package net.thechance.mena.identity.presentation.screen.profile.imageCropper.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.save
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

@Composable
fun SaveButton(
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
) {
    PrimaryButton(
        text = stringResource(Res.string.save),
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = SquircleShape(Theme.radius.md),
        contentPadding = PaddingValues(horizontal = Theme.spacing._16, vertical = 13.dp),
        isEnabled = isEnabled,
        isLoading = isLoading
    )
}

@Preview
@Composable
private fun SaveButtonPreview() {
    MenaTheme {
        SaveButton(onClick = { })
    }
}

@Preview
@Composable
private fun SaveButtonDisabledPreview() {
    MenaTheme {
        SaveButton(
            onClick = { },
            isEnabled = false
        )
    }
}