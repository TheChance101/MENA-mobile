package net.thechance.mena.identity.domain.repository

import kotlinx.coroutines.flow.StateFlow
import net.thechance.mena.identity.domain.util.AppLanguage

interface SettingsRepository {
    suspend fun applyLanguage(appLanguage: AppLanguage)
    fun observeAppLanguage(): StateFlow<AppLanguage>
    fun getCurrentAppLanguage(): AppLanguage
}