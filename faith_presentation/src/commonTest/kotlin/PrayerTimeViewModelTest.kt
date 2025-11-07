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

@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class, ExperimentalCoroutinesApi::class)
class PrayerTimeViewModelTest {

    private lateinit var viewModel: PrayerTimeViewModel
    private lateinit var prayerTimeRepository: PrayerTimeRepository
    private lateinit var addressesRepository: AddressesRepository
    private lateinit var locationService: LocationService
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        prayerTimeRepository = mock(MockMode.autofill)
        addressesRepository = mock(MockMode.autofill)
        locationService = LocationService(addressesRepository)
    }

    @Test
    fun `should set address after loading prayer times`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress
        everySuspend { prayerTimeRepository.getPrayerTimes(any(), any()) } returns fakePrayerTimes

        viewModel = PrayerTimeViewModel(prayerTimeRepository, locationService, testDispatcher)


        val state = viewModel.uiState.value
        assertEquals("Baghdad, Iraq", state.address)
    }

    @Test
    fun `should set hijri date after loading prayer times`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress
        everySuspend { prayerTimeRepository.getPrayerTimes(any(), any()) } returns fakePrayerTimes

        viewModel = PrayerTimeViewModel(prayerTimeRepository, locationService, testDispatcher)


        val state = viewModel.uiState.value
        assertTrue(state.currentDate.isNotEmpty())
    }

    @Test
    fun `should call getPrayerTimes once`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress
        everySuspend { prayerTimeRepository.getPrayerTimes(any(), any()) } returns fakePrayerTimes

        viewModel = PrayerTimeViewModel(prayerTimeRepository, locationService, testDispatcher)


        verifySuspend(mode = exactly(1)) { prayerTimeRepository.getPrayerTimes(any(), any()) }
    }


    @Test
    fun `should set next prayer name when prayer times loaded`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress
        everySuspend { prayerTimeRepository.getPrayerTimes(any(), any()) } returns fakePrayerTimes

        viewModel = PrayerTimeViewModel(prayerTimeRepository, locationService, testDispatcher)

        val nextName = viewModel.uiState.value.nextPrayerName
        assertTrue(
            nextName in listOf(
                PrayerName.FAJR,
                PrayerName.DHUHR,
                PrayerName.ASR,
                PrayerName.MAGHRIB,
                PrayerName.ISHA
            )
        )
    }

    @Test
    fun `should handle error when prayer time repository throws`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress
        everySuspend {
            prayerTimeRepository.getPrayerTimes(
                any(),
                any()
            )
        } throws Exception("Network error")

        viewModel = PrayerTimeViewModel(prayerTimeRepository, locationService, testDispatcher)

        assertTrue(viewModel.uiState.value.prayerTimes.isEmpty())
    }

    @Test
    fun `should emit correct navigation effects`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns fakeAddress
        everySuspend { prayerTimeRepository.getPrayerTimes(any(), any()) } returns fakePrayerTimes

        viewModel = PrayerTimeViewModel(prayerTimeRepository, locationService, testDispatcher)

        viewModel.uiEffect.test {
            viewModel.onBackClick()
            assertEquals(PrayerTimeEffect.NavigateBack, awaitItem())

            viewModel.onNextDateClick()
            assertEquals(PrayerTimeEffect.NavigateNextDate, awaitItem())

            viewModel.onPrevDateClick()
            assertEquals(PrayerTimeEffect.NavigatePrevDate, awaitItem())

            viewModel.onDateDropdownClick()
            assertEquals(PrayerTimeEffect.NavigateCalenderDialog, awaitItem())

            viewModel.onLocationClick()
            assertEquals(PrayerTimeEffect.NavigateToAddressesScreen, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
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
