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
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import net.thechance.mena.faith.domain.repository.PrayerTimeRepository
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.domain.repository.AddressesRepository
import net.thechance.mena.identity.domain.service.LocationService
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
class PrayerTimeViewModelTest {

    private var testDispatcher: TestDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: PrayerTimeViewModel
    private lateinit var prayerTimeRepository: PrayerTimeRepository
    private lateinit var addressesRepository: AddressesRepository
    private lateinit var locationService: LocationService

    @BeforeTest
    fun setup() {
        prayerTimeRepository = mock(MockMode.autofill)
        addressesRepository = mock(MockMode.autofill)

        everySuspend { prayerTimeRepository.getPrayerTimes(any(), any()) } returns fakePrayerTimes
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress

        locationService = LocationService(addressesRepository)
    }

    @Test
    fun `init should navigate to addresses screen when address is null`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns null

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        viewModel.uiEffect.test {
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(PrayerTimeEffect.NavigateToAddressesScreen, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init should navigate to addresses screen when addressLine is empty`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns emptyAddressLine

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        viewModel.uiEffect.test {
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(PrayerTimeEffect.NavigateToAddressesScreen, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init should update address in state when address is valid`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Baghdad, Iraq", viewModel.uiState.value.address)
    }

    @Test
    fun `init should load prayer times and filter SUNRISE`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(5, state.prayerTimes.size) // 6 prayers - 1 (SUNRISE)
        assertTrue(state.prayerTimes.none { it.name == PrayerName.SUNRISE })
    }

    @Test
    fun `init should set hijri date when prayer times loaded`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.currentDate.isNotEmpty())
    }

    @Test
    fun `init should set next prayer name to default when prayer times loaded`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(
            state.nextPrayerName in listOf(
                PrayerName.FAJR,
                PrayerName.DHUHR,
                PrayerName.ASR,
                PrayerName.MAGHRIB,
                PrayerName.ISHA
            )
        )
    }

    @Test
    fun `init should handle error when loading prayer times fails`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress
        everySuspend {
            prayerTimeRepository.getPrayerTimes(any(), any())
        } throws Exception("Network error")

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.prayerTimes.isEmpty())
    }

    @Test
    fun `init should call prayer times repository when address is valid`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend(mode = exactly(1)) {
            prayerTimeRepository.getPrayerTimes(any(), any())
        }
    }

    @Test
    fun `getValidatedAddress should return null when address is null`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns null

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        viewModel.uiEffect.test {
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(PrayerTimeEffect.NavigateToAddressesScreen, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getValidatedAddress should return null when addressLine is empty`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns emptyAddressLine

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        viewModel.uiEffect.test {
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(PrayerTimeEffect.NavigateToAddressesScreen, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getValidatedAddress should update state and return address when valid`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Baghdad, Iraq", viewModel.uiState.value.address)
        verifySuspend(mode = exactly(1)) {
            addressesRepository.getActiveAddress()
        }
    }

    @Test
    fun `onPrayerTimesSuccess should filter SUNRISE and update state`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(5, state.prayerTimes.size)
        assertTrue(state.prayerTimes.none { it.name == PrayerName.SUNRISE })
        assertTrue(state.currentDate.isNotEmpty())
    }

    @Test
    fun `onBackClick should emit NavigateBack effect`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.onBackClick()
            assertEquals(PrayerTimeEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onNextDateClick should emit NavigateNextDate effect`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.onNextDateClick()
            assertEquals(PrayerTimeEffect.NavigateNextDate, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onPrevDateClick should emit NavigatePrevDate effect`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.onPrevDateClick()
            assertEquals(PrayerTimeEffect.NavigatePrevDate, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDateDropdownClick should emit NavigateCalenderDialog effect`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.onDateDropdownClick()
            assertEquals(PrayerTimeEffect.NavigateCalenderDialog, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onChangeLocation should emit NavigateToAddressesScreen effect`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.onChangeLocation()
            assertEquals(PrayerTimeEffect.NavigateToAddressesScreen, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `handleInvalidAddress should emit NavigateToAddressesScreen effect`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.onChangeLocation()
            assertEquals(PrayerTimeEffect.NavigateToAddressesScreen, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `prayer times should remain empty when loading fails`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress
        everySuspend {
            prayerTimeRepository.getPrayerTimes(any(), any())
        } throws Exception("Network error")

        viewModel = PrayerTimeViewModel(
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.prayerTimes.isEmpty())
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

        val emptyAddressLine = fakeAddress.copy(addressLine = "")
    }
}