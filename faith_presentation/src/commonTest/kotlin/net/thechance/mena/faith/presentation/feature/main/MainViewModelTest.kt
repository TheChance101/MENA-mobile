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
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import net.thechance.mena.faith.domain.model.LastAyahForTilawah
import net.thechance.mena.faith.domain.repository.PrayerTimeRepository
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.snackbar.SnackbarHandler
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
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
class MainViewModelTests {

    private var testDispatcher: TestDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: MainViewModel
    private lateinit var quranRepository: QuranRepository
    private lateinit var prayerTimeRepository: PrayerTimeRepository
    private lateinit var addressesRepository: AddressesRepository
    private lateinit var locationService: LocationService

    @BeforeTest
    fun setup() {
        startKoin {
            modules(module { single { mock<SnackbarHandler>(MockMode.autofill) } })
        }
        quranRepository = mock(MockMode.autofill)
        prayerTimeRepository = mock(MockMode.autofill)
        addressesRepository = mock(MockMode.autofill)

        everySuspend { quranRepository.getLastAyahForTilawah() } returns fakeAyah
        everySuspend { prayerTimeRepository.getPrayerTimes(any(), any()) } returns fakePrayerTimes
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress

        locationService = LocationService(addressesRepository)
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `init should update address in state when address is valid`() = runTest {

        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress

        viewModel = MainViewModel(
            quranRepository = quranRepository,
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Baghdad, Iraq", viewModel.uiState.value.address)
    }

    @Test
    fun `onChangeLocation should navigate to enable location when address is not empty`() =
        runTest {

            everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress

            viewModel = MainViewModel(
                quranRepository = quranRepository,
                prayerTimeRepository = prayerTimeRepository,
                locationService = locationService,
                dispatcher = testDispatcher
            )

            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.uiEffect.test {
                viewModel.onLocationClick()
                assertEquals(MainScreenEffect.NavigateToAddressesScreen, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onChangeLocation should navigate to my location when address is empty`() = runTest {

        everySuspend { addressesRepository.getActiveAddress() } returns emptyAddress

        viewModel = MainViewModel(
            quranRepository = quranRepository,
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.address)

        viewModel.uiEffect.test {
            viewModel.onLocationClick()
            assertEquals(MainScreenEffect.NavigateToAddressesScreen, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init should set isLoading to false during prayer times loading`() = runTest {

        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress

        viewModel = MainViewModel(
            quranRepository = quranRepository,
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        assertFalse(viewModel.uiState.value.isLoading)

        testDispatcher.scheduler.advanceUntilIdle()
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `init should set isLoading to false after prayer times error`() = runTest {

        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress
        everySuspend {
            prayerTimeRepository.getPrayerTimes(any(), any())
        } throws Exception("Network error")

        viewModel = MainViewModel(
            quranRepository = quranRepository,
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `init should populate prayerTimesUiState when prayer times loaded`() = runTest {

        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress

        viewModel = MainViewModel(
            quranRepository = quranRepository,
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.prayerTimesUiState != null)
        assertTrue(state.prayerTimesUiState?.prayers?.isNotEmpty() == true)
    }

    @Test
    fun `init should populate tilawahUiState when last ayah loaded`() = runTest {

        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress

        viewModel = MainViewModel(
            quranRepository = quranRepository,
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.tilawahUiState != null)
        assertEquals(SURAH_ID, state.tilawahUiState?.surahId)
    }

    @Test
    fun `init should handle error when loading last ayah fails`() = runTest {

        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress
        everySuspend {
            quranRepository.getLastAyahForTilawah()
        } throws Exception("Database error")

        viewModel = MainViewModel(
            quranRepository = quranRepository,
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `init should call both prayer times and tilawah repositories`() = runTest {

        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress

        viewModel = MainViewModel(
            quranRepository = quranRepository,
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend(mode = exactly(1)) {
            prayerTimeRepository.getPrayerTimes(any(), any())
        }
        verifySuspend(mode = exactly(1)) {
            quranRepository.getLastAyahForTilawah()
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `prayer times should remain empty when loading fails`() = runTest {

        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress
        everySuspend {
            prayerTimeRepository.getPrayerTimes(any(), any())
        } throws Exception("Network error")

        viewModel = MainViewModel(
            quranRepository = quranRepository,
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.prayerTimes.isEmpty())
        assertTrue(viewModel.uiState.value.prayerTimesUiState == null)
    }

    @Test
    fun `onChangeLocation should navigate to EnableLocation when address is valid`() = runTest {

        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress

        val viewModel = MainViewModel(
            quranRepository = quranRepository,
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.onLocationClick()
            assertEquals(MainScreenEffect.NavigateToAddressesScreen, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onPrayerTimeClick should emit NavigateToPrayerTime effect`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val viewModel = MainViewModel(
            quranRepository = quranRepository,
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        viewModel.uiEffect.test {
            viewModel.onPrayerTimeClick()

            assertEquals(MainScreenEffect.NavigateToPrayerTime, awaitItem())
        }
    }

    @Test
    fun `onChangeLocation should navigate to MyLocation when address is empty`() = runTest {
        val emptyAddress = fakeAddress.copy(addressLine = "")
        everySuspend { addressesRepository.getActiveAddress() } returns emptyAddress

        val viewModel = MainViewModel(
            quranRepository = quranRepository,
            prayerTimeRepository = prayerTimeRepository,
            locationService = locationService,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.onLocationClick()
            assertEquals(MainScreenEffect.NavigateToAddressesScreen, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }


    private companion object {
        const val SURAH_ID = 1
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

        @OptIn(ExperimentalUuidApi::class)
        val fakeAddress = Address(
            id = Uuid.random(),
            latitude = 33.3152,
            longitude = 44.3661,
            addressLine = "Baghdad, Iraq",
            addressType = AddressType.Home
        )

        val fakeAyah = LastAyahForTilawah(
            number = AYAH_NUMBER,
            surahId = SURAH_ID,
        )

        val emptyAddress = fakeAddress.copy(addressLine = "")
    }
}