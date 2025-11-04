package net.thechance.mena

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import net.thechance.mena.appEntryPoint.EntryPoint
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.identity.domain.service.LocalizationService
import net.thechance.mena.identity.domain.util.AppLanguage
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val localizationService = koinInject<LocalizationService>()
    val scope = rememberCoroutineScope()
    val currentLanguage by localizationService.observeLanguage().stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = AppLanguage.ENGLISH
    ).collectAsStateWithLifecycle()
    MenaTheme(
        language = currentLanguage.iso,
        content =
            {
                SetStatusBarIconsDark()
                EntryPoint()
            }
    )
}
