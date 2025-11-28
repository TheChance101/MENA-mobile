package net.thechance.mena.dukan.presentation.component.state

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.no_internet
import mena.dukan_presentation.generated.resources.no_internet_dark
import mena.dukan_presentation.generated.resources.no_internet_message
import mena.dukan_presentation.generated.resources.no_internet_title
import mena.dukan_presentation.generated.resources.retry_button
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.navigation.LocalDarkTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoInternetContent(
    onRetry: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isDark = LocalDarkTheme.current
    val icon = if(isDark) Res.drawable.no_internet_dark else  Res.drawable.no_internet

    Column(
        modifier = modifier.padding(horizontal = Theme.spacing._16),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ImageWithTextContainer(
            foregroundImageRes = icon,
            header = {
                Text(
                    modifier = Modifier
                        .padding(bottom = Theme.spacing._2),
                    text = stringResource(Res.string.no_internet_title),
                    color = Theme.colorScheme.shadePrimary,
                    style = Theme.typography.title.small,
                )
            },
            bodyText = stringResource(Res.string.no_internet_message),
        )
        PrimaryButton(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(Res.string.retry_button),
            onClick = onRetry,
            isLoading = isLoading,
            contentPadding = PaddingValues(
                vertical = Theme.spacing._8,
                horizontal = Theme.spacing._16
            )
        )
    }
}

@Preview
@Composable
private fun NoInternetContentPreview() {
    MenaTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colorScheme.background.surface)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NoInternetContent(onRetry = {})
        }
    }
}