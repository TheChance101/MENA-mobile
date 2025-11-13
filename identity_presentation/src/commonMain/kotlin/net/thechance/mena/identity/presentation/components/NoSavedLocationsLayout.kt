package net.thechance.mena.identity.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.add_location_button
import mena.identity_presentation.generated.resources.ic_location_saved_empty
import mena.identity_presentation.generated.resources.location_saved_empty_desc
import mena.identity_presentation.generated.resources.no_saved_locations_message
import mena.identity_presentation.generated.resources.no_saved_locations_title
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoSavedLocationsLayout(
    onAddLocationClicked: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    LocationEmptySection(
        iconPainter = painterResource(Res.drawable.ic_location_saved_empty),
        iconContentDescriptionResource = Res.string.location_saved_empty_desc,
        titleResource = Res.string.no_saved_locations_title,
        messageResource = Res.string.no_saved_locations_message,
        buttonTextResource = Res.string.add_location_button,
        onButtonClicked = onAddLocationClicked,
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
            .background(Theme.colorScheme.background.surface))
        {
            NoSavedLocationsLayout(
                onAddLocationClicked = {},
                modifier = Modifier.padding(horizontal = Theme.spacing._24)
            )
        }
    }
}