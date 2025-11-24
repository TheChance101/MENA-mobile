package net.thechance.mena.identity.presentation.util

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.thechance.mena.identity.domain.repository.SettingsRepository

actual class AppThemeChanger(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        coroutineScope.launch {
            settingsRepository.observeAppTheme().collectLatest { theme ->
                settingsRepository.applyAppTheme(theme)
            }
        }
    }
}
