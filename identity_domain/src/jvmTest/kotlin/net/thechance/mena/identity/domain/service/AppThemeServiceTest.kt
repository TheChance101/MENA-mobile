package net.thechance.mena.identity.domain.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import net.thechance.mena.identity.domain.repository.SettingsRepository
import net.thechance.mena.identity.domain.util.AppTheme
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class AppThemeServiceTest {

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var appThemeService: AppThemeService

    @Before
    fun setUp() {
        settingsRepository = mockk(relaxed = true)
        appThemeService = AppThemeService(settingsRepository)
    }

    @Test
    fun `observeAppTheme should call repository and return correct flow`() {
        val expectedFlow = MutableStateFlow(AppTheme.DARK)
        every { settingsRepository.observeAppTheme() } returns expectedFlow
        val result = appThemeService.observeAppTheme()
        verify(exactly = 1) { settingsRepository.observeAppTheme() }
        assertEquals(expectedFlow, result)
        assertEquals(AppTheme.DARK, result.value)
    }
}
