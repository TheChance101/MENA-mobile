package net.thechance.mena.identity.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.add_location_button
import mena.identity_presentation.generated.resources.ic_location_saved_empty
import mena.identity_presentation.generated.resources.location_saved_empty_desc
import mena.identity_presentation.generated.resources.no_saved_locations_message
import mena.identity_presentation.generated.resources.no_saved_locations_title
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoSavedLocationsComponent(
    onAddLocationClicked: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_location_saved_empty),
            modifier = Modifier
                .size(128.dp)
                .padding(bottom = 24.dp),
            contentDescription = stringResource(Res.string.location_saved_empty_desc),
        )

        Text(
            text = stringResource(Res.string.no_saved_locations_title),
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = stringResource(Res.string.no_saved_locations_message),
            modifier = Modifier.padding(top = Theme.spacing._8, bottom = Theme.spacing._24),
            textAlign = TextAlign.Center,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary
        )

        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.add_location_button),
            contentPadding = PaddingValues(horizontal = Theme.spacing._24),
            onClick = onAddLocationClicked,
            isEnabled = !isLoading,
            isLoading = isLoading
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoSavedLocationsComponentPreview() {
    MenaTheme {
        Box(
            modifier = Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surface)) {
            NoSavedLocationsComponent(
                onAddLocationClicked = {},
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}