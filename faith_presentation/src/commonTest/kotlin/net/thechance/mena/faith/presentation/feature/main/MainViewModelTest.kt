package net.thechance.mena.faith.presentation.feature.main

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import net.thechance.mena.faith.domain.model.LastAyahForTilawah
import net.thechance.mena.faith.domain.repository.PrayerTimeRepository
import net.thechance.mena.faith.domain.repository.QuranRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private var testDispatcher: TestDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: MainViewModel
    private lateinit var quranRepository: QuranRepository
    private lateinit var prayerTimeRepository: PrayerTimeRepository

    @OptIn(ExperimentalTime::class)
    @BeforeTest
    fun setup() {
        quranRepository = mock(MockMode.autofill)
        prayerTimeRepository = mock(MockMode.autofill)

        everySuspend { quranRepository.getLastAyahForTilawah() } returns fakeAyah
        everySuspend { prayerTimeRepository.getPrayerTimes(any(), any()) } returns fakePrayerTimes

        viewModel = MainViewModel(quranRepository, prayerTimeRepository, testDispatcher)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `viewModel should load prayer times`() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertEquals(fakePrayerTimes.size, state.prayerTimes.size)
        assertTrue(state.prayerTimes.isNotEmpty())
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `init should handle prayer times error`() = runTest {
        everySuspend {
            prayerTimeRepository.getPrayerTimes(
                any(),
                any()
            )
        } throws Exception("Network error")

        val failingViewModel = MainViewModel(quranRepository, prayerTimeRepository, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()
        val state = failingViewModel.uiState.value

        assertFalse(state.isLoading)
        assertTrue(state.prayerTimes.isEmpty())
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `onContinueTilawahClick should emit NavigateToSurah effect`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.onContinueTilawahClick(SURAH_ID, SURAH_NAME, AYAH_NUMBER)

            assertEquals(
                MainScreenEffect.NavigateToSurah(SURAH_ID, SURAH_NAME, AYAH_NUMBER),
                awaitItem()
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `onQuranClick should emit NavigateToQuran effect`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.onQuranClick()

            assertEquals(MainScreenEffect.NavigateToQuran, awaitItem())
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `onQiblahClick should emit NavigateToQiblah effect`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.onQiblahClick()

            assertEquals(MainScreenEffect.NavigateToQiblah, awaitItem())
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `onMosquesClick should emit NavigateToMosques effect`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.onMosquesClick()
            assertEquals(MainScreenEffect.NavigateToMosques, awaitItem())
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `onPrayerTimeClick should emit NavigateToPrayerTime effect`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.onPrayerTimeClick()

            assertEquals(MainScreenEffect.NavigateToPrayerTime, awaitItem())
        }
    }

    @Test
    fun `refreshTilawah should load last ayah for tilawah`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.refreshTilawah()
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend(mode = exactly(2)) { quranRepository.getLastAyahForTilawah() } // once in init, once in refresh
    }

    private companion object {
        const val SURAH_ID = 1
        const val SURAH_NAME = "Al-Fatihah"
        const val AYAH_NUMBER = 1

        @OptIn(ExperimentalTime::class)
        private val now = Instant.fromEpochSeconds(1_700_000_000)

        @OptIn(ExperimentalTime::class)
        val fakePrayerTimes = listOf(
            PrayerTime(PrayerName.FAJR, now, "1446-04-10"),
            PrayerTime(PrayerName.SUNRISE, now + 1.hours, "1446-04-10"),
            PrayerTime(PrayerName.DHUHR, now + 6.hours, "1446-04-10"),
            PrayerTime(PrayerName.ASR, now + 9.hours, "1446-04-10"),
            PrayerTime(PrayerName.MAGHRIB, now + 12.hours, "1446-04-10"),
            PrayerTime(PrayerName.ISHA, now + 13.hours, "1446-04-10")
        )

        val fakeAyah = LastAyahForTilawah(
            number = 1,
            surahId = SURAH_ID,
            surahName = SURAH_NAME
        )
    }
}
