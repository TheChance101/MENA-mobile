package net.thechance.mena.identity.data.repository

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import net.thechance.mena.identity.data.dataSource.local.setting.appLanguage
import net.thechance.mena.identity.domain.repository.ProfileRepository

class ProfileRepositoryImpl(
    private val settings: Settings
) : ProfileRepository {
    private val observableLanguage: MutableStateFlow<String> = MutableStateFlow(settings.appLanguage)
    override fun applyLanguage(languageIso: String) {
        settings.appLanguage = languageIso.also { observableLanguage.value = it }
    }
    override fun observeLanguage(): StateFlow<String> {
        return observableLanguage
    }
    override fun getCurrentLanguage(): String {
       return settings.appLanguage
    }
}