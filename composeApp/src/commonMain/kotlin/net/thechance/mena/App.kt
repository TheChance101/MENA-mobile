package net.thechance.mena

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.appEntryPoint.EntryPoint
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.identity.domain.service.AppThemeService
import net.thechance.mena.identity.domain.service.LocalizationService
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
@Preview
fun App(
    isSystemDarkTheme: Boolean = isSystemInDarkTheme()
) {
    val localizationService = koinInject<LocalizationService>()
    val appThemeService = koinInject<AppThemeService>()
    val currentLanguage by localizationService.observeLanguage().collectAsStateWithLifecycle()
    val currentTheme by appThemeService.observeAppTheme().collectAsStateWithLifecycle()

    MenaTheme(
        language = currentLanguage.iso,
        appTheme = currentTheme.name,
        isSystemInDarkTheme = isSystemDarkTheme,
        content = {
            SetSystemBarsAppearance(currentTheme, isSystemDarkTheme)
            EntryPoint()
        }
    )
}
