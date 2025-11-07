package net.thechance.mena.faith.presentation.feature.prayertime

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import net.thechance.mena.faith.domain.entity.Location
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import net.thechance.mena.faith.domain.repository.PrayerTimeRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class PrayerTimeViewModelTest {

    private val prayerTimeRepository: PrayerTimeRepository = mock(MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: PrayerTimeViewModel

    @BeforeTest
    fun setup() {
        viewModel = PrayerTimeViewModel(prayerTimeRepository, testDispatcher)
    }


    @Test
    fun `onBackClick should emit NavigateBack effect`() = runTest {
        viewModel.uiEffect.test {
            viewModel.onBackClick()
            assertEquals(PrayerTimeEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onNextDateClick should emit NavigateNextDate effect`() = runTest {
        viewModel.uiEffect.test {
            viewModel.onNextDateClick()
            assertEquals(PrayerTimeEffect.NavigateNextDate, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onPrevDateClick should emit NavigatePrevDate effect`() = runTest {
        viewModel.uiEffect.test {
            viewModel.onPrevDateClick()
            assertEquals(PrayerTimeEffect.NavigatePrevDate, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDateDropdownClick should emit NavigateCalenderDialog effect`() = runTest {
        viewModel.uiEffect.test {
            viewModel.onDateDropdownClick()
            assertEquals(PrayerTimeEffect.NavigateCalenderDialog, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onChangeLocationClick should emit NavigateChangeLocation effect`() = runTest {
        viewModel.uiEffect.test {
            viewModel.onChangeLocation()
            assertEquals(PrayerTimeEffect.NavigateToChangeLocation, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `initialize view model should filter out SUNRISE prayer`() = runTest {
        everySuspend {
            prayerTimeRepository.getPrayerTimes(any(), any<Location>(), any())
        } returns samplePrayerTimes

        val viewModel = PrayerTimeViewModel(
            prayerTimeRepository = prayerTimeRepository,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.runCurrent()

        val state = viewModel.uiState.value

        assertEquals(false, state.prayerTimes.any { it.name == PrayerName.SUNRISE })
    }


    private companion object {
        @OptIn(ExperimentalTime::class)
        val samplePrayerTimes = listOf(
            PrayerTime(PrayerName.FAJR, createInstant(5, 0), hijriDate = "22 Sufar 1447H"),
            PrayerTime(PrayerName.SUNRISE, createInstant(6, 15), hijriDate = "22 Sufar 1447H"),
            PrayerTime(PrayerName.DHUHR, createInstant(12, 0), hijriDate = "22 Sufar 1447H"),
            PrayerTime(PrayerName.ASR, createInstant(15, 30), hijriDate = "22 Sufar 1447H"),
            PrayerTime(PrayerName.MAGHRIB, createInstant(18, 0), hijriDate = "22 Sufar 1447H"),
            PrayerTime(PrayerName.ISHA, createInstant(19, 30), hijriDate = "22 Sufar 1447H")
        )

        @OptIn(ExperimentalTime::class)
        fun createInstant(hour: Int, minute: Int): Instant {
            val totalMillis = (hour * 60L + minute) * 60L * 1000L
            return Instant.fromEpochMilliseconds(totalMillis)
        }
    }
}
