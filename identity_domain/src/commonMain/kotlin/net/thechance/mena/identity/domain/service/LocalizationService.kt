package net.thechance.mena.identity.domain.service

import kotlinx.coroutines.flow.Flow
import net.thechance.mena.identity.domain.repository.UserRepository

class LocalizationService(
    private val userRepository: UserRepository
) {
    fun observeLanguage(): Flow<String> =
        userRepository.observeAppLanguage()

    fun getCurrentLanguage(): String =
        userRepository.getCurrentAppLanguage()
}