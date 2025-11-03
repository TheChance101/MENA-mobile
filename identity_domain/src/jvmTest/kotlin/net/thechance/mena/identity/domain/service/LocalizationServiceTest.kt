package net.thechance.mena.identity.domain.service

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.domain.repository.UserRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalizationServiceTest {

    private val currentLanguage = "ar"

    private val userRepository: UserRepository = mockk()
    private val localizationService = LocalizationService(userRepository)

    @Test
    fun `getCurrentLanguage() should return current language`() = runTest{
        coEvery { userRepository.getCurrentAppLanguage() } returns currentLanguage

        val result = localizationService.getCurrentLanguage()

        assertEquals(currentLanguage ,result)

    }

    @Test
    fun `observeAppLanguage() should return current app language`() = runTest{

        val fakeLanguageFlow = MutableStateFlow(currentLanguage)

        coEvery { userRepository.observeAppLanguage() } returns fakeLanguageFlow

        val result = localizationService.observeLanguage()

        assertEquals(currentLanguage, result.first())

        result.test {
            fakeLanguageFlow.emit("en")
            assertEquals("en", result.first())
            cancelAndIgnoreRemainingEvents()
        }

    }
}