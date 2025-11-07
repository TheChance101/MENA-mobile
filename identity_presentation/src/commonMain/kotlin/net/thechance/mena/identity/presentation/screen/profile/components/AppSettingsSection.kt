package net.thechance.mena.identity.presentation.screen.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.ic_language
import mena.identity_presentation.generated.resources.ic_theme
import mena.identity_presentation.generated.resources.profile_app_settings_header
import mena.identity_presentation.generated.resources.profile_language
import mena.identity_presentation.generated.resources.profile_language_english
import mena.identity_presentation.generated.resources.profile_theme
import mena.identity_presentation.generated.resources.profile_theme_light
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.util.mapLanguage
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AppSettingsSection(
    currentLanguage: String,
    onLanguageClicked: () -> Unit,
    onThemeClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(top = Theme.spacing._24),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        Text(
            text = stringResource(Res.string.profile_app_settings_header),
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadeSecondary,
            textAlign = TextAlign.Center,
        )
        SettingItem(
            title = stringResource(Res.string.profile_language),
            leadingIcon = painterResource(Res.drawable.ic_language),
            onClick = onLanguageClicked,
            trailingText =stringResource(mapLanguage(currentLanguage))
        )
        SettingItem(
            title = stringResource(Res.string.profile_theme),
            leadingIcon = painterResource(Res.drawable.ic_theme),
            onClick = onThemeClicked,
            trailingText = stringResource(Res.string.profile_theme_light)
        )
    }
}

@Preview
@Composable
fun PreviewAppSettingsSection() {
    MenaTheme {

        AppSettingsSection(
            "", {},{}
        )
    }
}
