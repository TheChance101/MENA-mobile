package net.thechance.mena.identity.domain.service

import kotlinx.coroutines.flow.StateFlow
import net.thechance.mena.identity.domain.repository.SettingsRepository
import net.thechance.mena.identity.domain.util.AppLanguage

class LocalizationService(
    private val settingsRepository: SettingsRepository
) {
    fun observeLanguage(): StateFlow<AppLanguage> =
        settingsRepository.observeAppLanguage()

    fun getCurrentLanguage(): AppLanguage =
        settingsRepository.getCurrentAppLanguage()
}