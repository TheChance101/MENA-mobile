package net.thechance.mena.faith.presentation.feature.prayertime


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
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import net.thechance.mena.faith.domain.repository.PrayerTimeRepository
import net.thechance.mena.faith.domain.service.PrayerTimeService
import net.thechance.mena.faith.presentation.base.snackbar.SnackbarHandler
import net.thechance.mena.faith.presentation.utils.IslamicDate
import net.thechance.mena.faith.presentation.utils.IslamicDateCalculator
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.domain.repository.AddressesRepository
import net.thechance.mena.identity.domain.service.LocationService
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class, ExperimentalCoroutinesApi::class)
class PrayerTimeViewModelTest {

    private lateinit var viewModel: PrayerTimeViewModel
    private lateinit var prayerTimeRepository: PrayerTimeRepository
    private lateinit var addressesRepository: AddressesRepository
    private lateinit var locationService: LocationService
    private lateinit var islamicDateCalculator: IslamicDateCalculator
    private lateinit var prayerTimeService: PrayerTimeService
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        startKoin {
            modules(module { single { mock<SnackbarHandler>(MockMode.autofill) } })
        }

        prayerTimeRepository = mock(MockMode.autofill)
        islamicDateCalculator = mock(MockMode.autofill)
        prayerTimeService = PrayerTimeService(prayerTimeRepository)
        addressesRepository = mock(MockMode.autofill)
        locationService = LocationService(addressesRepository)
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `should set address after loading prayer times`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress
        everySuspend { prayerTimeRepository.getPrayerTimes(any(), any()) } returns fakePrayerTimes

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository,
            locationService,
            prayerTimeService,
            islamicDateCalculator,
            testDispatcher
        )

        val state = viewModel.uiState.value
        assertEquals("Baghdad, Iraq", state.address)
    }

    @Test
    fun `should call getPrayerTimes once`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress
        everySuspend { prayerTimeRepository.getPrayerTimes(any(), any()) } returns fakePrayerTimes

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository,
            locationService,
            prayerTimeService,
            islamicDateCalculator,
            testDispatcher
        )

        verifySuspend(mode = exactly(1)) { prayerTimeRepository.getPrayerTimes(any(), any()) }
    }

    @Test
    fun `should handle error when prayer time repository throws`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress
        everySuspend {
            prayerTimeRepository.getPrayerTimes(
                any(), any()
            )
        } throws Exception("Network error")

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository,
            locationService,
            prayerTimeService,
            islamicDateCalculator,
            testDispatcher
        )

        assertTrue(viewModel.uiState.value.prayerTimes.isEmpty())
    }

    @Test
    fun `should emit correct navigation effects`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress
        everySuspend { prayerTimeRepository.getPrayerTimes(any(), any()) } returns fakePrayerTimes

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository,
            locationService,
            prayerTimeService,
            islamicDateCalculator,
            testDispatcher
        )

        viewModel.uiEffect.test {
            viewModel.onBackClick()
            assertEquals(PrayerTimeEffect.NavigateBack, awaitItem())

            viewModel.onLocationClick()
            assertEquals(PrayerTimeEffect.NavigateToAddressesScreen, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDatePickerDismiss should disappear date picker dialog`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress
        everySuspend { prayerTimeRepository.getPrayerTimes(any(), any()) }

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository,
            locationService,
            prayerTimeService,
            islamicDateCalculator,
            testDispatcher
        )

        viewModel.onDatePickerDismiss()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isDatePickerShown)
    }

    @Test
    fun `onDateSelected should set selected date and hide date picker dialog`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress
        everySuspend { prayerTimeRepository.getPrayerTimes(any(), any()) }

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository,
            locationService,
            prayerTimeService,
            islamicDateCalculator,
            testDispatcher
        )

        viewModel.onDateSelected()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isDatePickerShown)
        assertEquals(
            viewModel.uiState.value.currentDate,
            viewModel.uiState.value.islamicDatePickerUiState.selectedIslamicDate
        )
        assertEquals(
            viewModel.uiState.value.islamicDatePickerUiState,
            PrayerTimeUiState.IslamicDatePickerUiState()
        )
    }

    @Test
    fun `onSelectedDateChange should update selected date and activate clear button`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress
        everySuspend { prayerTimeRepository.getPrayerTimes(any(), any()) } returns emptyList()

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository,
            locationService,
            prayerTimeService,
            islamicDateCalculator,
            testDispatcher
        )

        val selectedDate = IslamicDate(1, 1, 1447)
        viewModel.onSelectedDateChange(1, 1, 1447)
        advanceUntilIdle()

        assertEquals(
            PrayerTimeUiState.IslamicDatePickerUiState(
                selectedIslamicDate = selectedDate, isClearDateActive = true
            ), viewModel.uiState.value.islamicDatePickerUiState
        )
    }

    @Test
    fun `onDateDropdownClick should show date picker dialog`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress
        everySuspend { prayerTimeRepository.getPrayerTimes(any(), any()) } returns emptyList()

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository,
            locationService,
            prayerTimeService,
            islamicDateCalculator,
            testDispatcher
        )

        viewModel.onDateDropdownClick()

        assertTrue(viewModel.uiState.value.isDatePickerShown)
        assertEquals(
            PrayerTimeUiState.IslamicDatePickerUiState(
                selectedIslamicDate = viewModel.uiState.value.currentDate
            ), viewModel.uiState.value.islamicDatePickerUiState
        )
    }

    @Test
    fun `onPrevDateClick should handle month change when going from first day`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress
        everySuspend { prayerTimeRepository.getPrayerTimes(any(), any()) } returns emptyList()
        everySuspend {
            prayerTimeRepository.getPrayerTimesByHijriDate(
                any(), any(), any(), true
            )
        } returns emptyList()

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository,
            locationService,
            prayerTimeService,
            islamicDateCalculator,
            testDispatcher
        )

        viewModel.onSelectedDateChange(1, 1, 1447)
        viewModel.onDateSelected()
        advanceUntilIdle()

        viewModel.onPrevDateClick()
        advanceUntilIdle()

        assertEquals(IslamicDate(29, 12, 1446), viewModel.uiState.value.currentDate)
    }

    @Test
    fun `onNextDateClick should handle month change when going from last day`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress
        everySuspend { prayerTimeRepository.getPrayerTimes(any(), any()) } returns emptyList()
        everySuspend {
            prayerTimeRepository.getPrayerTimesByHijriDate(
                any(), any(), any(), true
            )
        } returns emptyList()

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository,
            locationService,
            prayerTimeService,
            islamicDateCalculator,
            testDispatcher
        )

        viewModel.onSelectedDateChange(29, 12, 1446)
        viewModel.onDateSelected()
        advanceUntilIdle()

        viewModel.onNextDateClick()
        advanceUntilIdle()

        assertEquals(IslamicDate(1, 1, 1447), viewModel.uiState.value.currentDate)
    }

    private companion object {
        private val now = Clock.System.now()

        val fakePrayerTimes = listOf(
            PrayerTime(PrayerName.FAJR, now + 1.hours, "1446-04-10"),
            PrayerTime(PrayerName.SUNRISE, now + 2.hours, "1446-04-10"),
            PrayerTime(PrayerName.DHUHR, now + 7.hours, "1446-04-10"),
            PrayerTime(PrayerName.ASR, now + 10.hours, "1446-04-10"),
            PrayerTime(PrayerName.MAGHRIB, now + 13.hours, "1446-04-10"),
            PrayerTime(PrayerName.ISHA, now + 14.hours, "1446-04-10")
        )

        val fakeAddress = Address(
            id = Uuid.random(),
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Baghdad, Iraq",
            addressType = AddressType.Home
        )
    }
}
