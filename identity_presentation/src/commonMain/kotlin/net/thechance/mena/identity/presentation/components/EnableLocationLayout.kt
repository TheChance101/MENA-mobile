package net.thechance.mena.identity.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.enable_location_message
import mena.identity_presentation.generated.resources.enable_location_permission_button
import mena.identity_presentation.generated.resources.enable_location_title
import mena.identity_presentation.generated.resources.ic_location
import mena.identity_presentation.generated.resources.location_icon_desc
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EnableLocationLayout(
    onEnablePermissionClicked: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    LocationEmptySection(
        iconPainter = painterResource(Res.drawable.ic_location),
        iconContentDescriptionResource = Res.string.location_icon_desc,
        titleResource = Res.string.enable_location_title,
        messageResource = Res.string.enable_location_message,
        buttonTextResource = Res.string.enable_location_permission_button,
        onButtonClicked = onEnablePermissionClicked,
        modifier = modifier,
        isLoading = isLoading
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    MenaTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surface)) {

            EnableLocationLayout(
                onEnablePermissionClicked = {},
                modifier = Modifier.padding(horizontal = Theme.spacing._24)
            )
        }
    }
}