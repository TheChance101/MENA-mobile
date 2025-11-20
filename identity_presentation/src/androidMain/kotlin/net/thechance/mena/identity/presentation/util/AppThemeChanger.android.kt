package net.thechance.mena.identity.presentation.util

import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.thechance.mena.identity.domain.repository.SettingsRepository
import net.thechance.mena.identity.domain.util.AppTheme
import android.content.res.Configuration
import android.content.Context

actual class AppThemeChanger(
private val context: Context,
private val settingsRepository: SettingsRepository
) {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var currentTheme: AppTheme = AppTheme.DEFAULT

    init {
        coroutineScope.launch {
            settingsRepository.observeAppTheme().collectLatest { theme ->
                currentTheme = if (theme == AppTheme.DEFAULT) {
                    val system = getSystemTheme()
                    settingsRepository.applyAppTheme(system)
                    system
                } else theme

                applyTheme(currentTheme)
            }
        }
    }

    private fun applyTheme(theme: AppTheme) {
        val mode = when (theme) {
            AppTheme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            AppTheme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            AppTheme.DEFAULT -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun getSystemTheme(): AppTheme {
        val uiMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return when (uiMode) {
            Configuration.UI_MODE_NIGHT_YES -> AppTheme.DARK
            Configuration.UI_MODE_NIGHT_NO -> AppTheme.LIGHT
            else -> AppTheme.LIGHT
        }
    }
}
