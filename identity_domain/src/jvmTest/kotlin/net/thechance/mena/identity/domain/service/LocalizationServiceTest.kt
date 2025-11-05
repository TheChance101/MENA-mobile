package net.thechance.mena.identity.domain.service

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.domain.repository.SettingsRepository
import net.thechance.mena.identity.domain.util.AppLanguage
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalizationServiceTest {

    private val currentLanguage = AppLanguage.ENGLISH

    private val settingsRepository: SettingsRepository = mockk()
    private val localizationService = LocalizationService(settingsRepository)

    @Test
    fun `getCurrentLanguage() should return current language`() = runTest {
        coEvery { settingsRepository.getCurrentAppLanguage() } returns currentLanguage

        val result = localizationService.getCurrentLanguage()

        assertEquals(currentLanguage, result)

    }

    @Test
    fun `observeAppLanguage() should return current app language`() = runTest {

        val fakeLanguageFlow = MutableStateFlow(currentLanguage)

        coEvery { settingsRepository.observeAppLanguage() } returns fakeLanguageFlow

        val result = localizationService.observeLanguage()

        assertEquals(currentLanguage, result.first())

        result.test {
            fakeLanguageFlow.emit(AppLanguage.ENGLISH)
            assertEquals(AppLanguage.ENGLISH, result.first())
            cancelAndIgnoreRemainingEvents()
        }

    }
}