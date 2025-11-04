package net.thechance.mena.identity.domain.service

import kotlinx.coroutines.flow.Flow
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.identity.domain.util.AppLanguage

class LocalizationService(
    private val userRepository: UserRepository
) {
    fun observeLanguage(): Flow<AppLanguage> =
        userRepository.observeAppLanguage()

    fun getCurrentLanguage(): AppLanguage =
        userRepository.getCurrentAppLanguage()
}