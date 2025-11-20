package net.thechance.mena.identity.domain.service

import kotlinx.coroutines.flow.StateFlow
import net.thechance.mena.identity.domain.repository.SettingsRepository
import net.thechance.mena.identity.domain.util.AppTheme

class AppThemeService(
    private val settingsRepository: SettingsRepository
) {
    fun observeAppTheme(): StateFlow<AppTheme> =
        settingsRepository.observeAppTheme()
}