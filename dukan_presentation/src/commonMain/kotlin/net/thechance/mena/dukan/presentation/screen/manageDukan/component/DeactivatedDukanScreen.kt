package net.thechance.mena.dukan.presentation.screen.manageDukan.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.deactivated_dukan
import mena.dukan_presentation.generated.resources.deactivated_dukan_dark
import mena.dukan_presentation.generated.resources.wrong_information_provided
import mena.dukan_presentation.generated.resources.your_dukan_has_been_deactivated
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.state.ImageWithTextContainer
import net.thechance.mena.dukan.presentation.navigation.LocalDarkTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DeactivatedDukanScreen(
    reason: String? = null
) {

    val isDark = LocalDarkTheme.current
    val icon = if (isDark) Res.drawable.deactivated_dukan_dark else Res.drawable.deactivated_dukan

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surface)
            .systemBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ImageWithTextContainer(
            foregroundImageRes = icon,
            haveBlurBackground = false,
            header = {
                    Text(
                        text = stringResource(Res.string.your_dukan_has_been_deactivated),
                        style = (Theme.typography.title.small).copy(textAlign = TextAlign.Center),
                        color = Theme.colorScheme.shadePrimary
                    )
            },
            bodyText = reason ?: stringResource(Res.string.wrong_information_provided),
        )
    }
}

@Preview
@Composable
fun DeactivatedDukanScreenPreview() {
    MenaTheme {
        DeactivatedDukanScreen()
    }
}
