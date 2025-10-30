package net.thechance.mena.identity.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface ProfileRepository {
    fun applyLanguage(languageIso: String)
    fun observeLanguage(): StateFlow<String>
    fun getCurrentLanguage(): String
}
