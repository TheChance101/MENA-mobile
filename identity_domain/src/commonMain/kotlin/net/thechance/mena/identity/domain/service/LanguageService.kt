package net.thechance.mena.identity.domain.service

import kotlinx.coroutines.flow.StateFlow
import net.thechance.mena.identity.domain.repository.ProfileRepository

class LocalizationService(
    private val profileRepository : ProfileRepository
) {
    fun applyLanguage(languageIso: String){
        profileRepository.applyLanguage(languageIso)
    }
    fun observeLanguage() : StateFlow<String>{
        return profileRepository.observeLanguage()
    }
    fun getCurrentLanguage() : String{
        return profileRepository.getCurrentLanguage()
    }
}