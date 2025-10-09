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
import mena.identity_presentation.generated.resources.enable_location_message
import mena.identity_presentation.generated.resources.enable_location_permission_button
import mena.identity_presentation.generated.resources.enable_location_title
import mena.identity_presentation.generated.resources.ic_location
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EnableLocationComponent(
    onEnablePermissionClicked: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {

        Icon(
            painter = painterResource(Res.drawable.ic_location),
            modifier = Modifier
                .size(128.dp)
                .padding(bottom = 16.dp),
            contentDescription = "location",
        )

        Text(
            text = stringResource(Res.string.enable_location_title),
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = stringResource(Res.string.enable_location_message),
            modifier = Modifier.padding(top = Theme.spacing._8, bottom = Theme.spacing._24),
            textAlign = TextAlign.Center,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary
        )

        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.enable_location_permission_button),
            contentPadding = PaddingValues(
                vertical = Theme.spacing._12,
                horizontal = Theme.spacing._16
            ),
            onClick = onEnablePermissionClicked,
            isEnabled = !isLoading,
            isLoading = isLoading
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EnableLocationComponentPreview() {
    MenaTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colorScheme.background.surface)
        ) {
            EnableLocationComponent(
                onEnablePermissionClicked = {},
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}