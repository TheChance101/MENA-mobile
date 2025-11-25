package net.thechance.mena.identity.data.repository

import com.russhwolf.settings.Settings
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.data.dataSource.local.setting.APP_LANGUAGE
import net.thechance.mena.identity.domain.util.AppLanguage
import net.thechance.mena.identity.domain.util.AppTheme
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class SettingsRepositoryImplTest {

    private lateinit var settingsRepository: SettingsRepositoryImpl
    private val settings: Settings = mockk(relaxed = true)

    @Before
    fun setUp() {
        settingsRepository = SettingsRepositoryImpl(settings)
    }

    @Test
    fun `getCurrentAppLanguage() should return default language when no language is set`() =
        runTest {
            val expectedLanguage = AppLanguage.DEFAULT
            val actualLanguage = settingsRepository.getCurrentAppLanguage()
            assertEquals(expectedLanguage, actualLanguage)
        }

    @Test
    fun `getCurrentAppLanguage() should return arabic when arabic is set`() = runTest {
        every { settings.getString(APP_LANGUAGE, "") } returns AppLanguage.ARABIC.iso
        val expectedLanguage = AppLanguage.ARABIC
        settingsRepository.applyLanguage(expectedLanguage)
        val actualLanguage = settingsRepository.getCurrentAppLanguage()
        assertEquals(expectedLanguage, actualLanguage)
    }

    @Test
    fun `observeAppLanguage() should return latest language when it changes`() = runTest {
        val expected = AppLanguage.ENGLISH
        settingsRepository.applyLanguage(AppLanguage.ENGLISH)
        val result = settingsRepository.observeAppLanguage()
        assertEquals(expected, result.value)
    }

    @Test
    fun `observeAppTheme() should return default theme when no theme is set`() = runTest {
        val expected = AppTheme.SYSTEM
        val result = settingsRepository.observeAppTheme()
        assertEquals(expected, result.value)
    }

    @Test
    fun `observeAppTheme() should return latest theme when it changes`() = runTest {
        val expected = AppTheme.DARK
        settingsRepository.applyAppTheme(AppTheme.DARK)
        val result = settingsRepository.observeAppTheme()
        assertEquals(expected, result.value)
    }
}
