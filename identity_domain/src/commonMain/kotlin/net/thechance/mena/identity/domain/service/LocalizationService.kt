package net.thechance.mena.identity.domain.service

import kotlinx.coroutines.flow.StateFlow
import net.thechance.mena.identity.domain.repository.UserRepository

class LocalizationService(
    private val userRepository: UserRepository
) {
    fun observeLanguage(): StateFlow<String> =
        userRepository.observeAppLanguage()

    fun getCurrentLanguage(): String =
        userRepository.getCurrentAppLanguage()
}