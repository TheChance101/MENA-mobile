package net.thechance.mena

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.appEntryPoint.EntryPoint
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.identity.domain.service.LocalizationService
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val localizationService = koinInject<LocalizationService>()
    val currentLanguage by localizationService.observeLanguage().collectAsStateWithLifecycle()
    MenaTheme(
        language = currentLanguage.iso,
        content = {
            SetStatusBarIconsDark()
            EntryPoint()
        }
    )
}
