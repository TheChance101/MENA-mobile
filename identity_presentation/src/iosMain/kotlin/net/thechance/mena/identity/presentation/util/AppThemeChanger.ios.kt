package net.thechance.mena.identity.presentation.util

import platform.UIKit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.thechance.mena.identity.domain.repository.SettingsRepository
import net.thechance.mena.identity.domain.util.AppTheme

actual class AppThemeChanger(
    private val settingsRepository: SettingsRepository
) {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
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
        val style = when (theme) {
            AppTheme.DARK -> UIUserInterfaceStyle.UIUserInterfaceStyleDark
            AppTheme.LIGHT -> UIUserInterfaceStyle.UIUserInterfaceStyleLight
            AppTheme.DEFAULT -> UIUserInterfaceStyle.UIUserInterfaceStyleUnspecified
        }
        UIApplication.sharedApplication.connectedScenes
            .filterIsInstance<UIWindowScene>()
            .flatMap { it.windows }
            .forEach { window ->
                (window as? UIWindow)?.overrideUserInterfaceStyle = style
            }
    }

    private fun getSystemTheme(): AppTheme {
        return when (UIScreen.mainScreen.traitCollection.userInterfaceStyle) {
            UIUserInterfaceStyle.UIUserInterfaceStyleDark -> AppTheme.DARK
            UIUserInterfaceStyle.UIUserInterfaceStyleLight -> AppTheme.LIGHT
            else -> AppTheme.LIGHT
        }
    }
}
