package net.thechance.mena.faith.presentation.feature.qiblah.compass

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.faith.domain.usecase.QiblahBearingCalculatorUseCase
import net.thechance.mena.faith.presentation.base.snackbar.SnackbarHandler
import net.thechance.mena.faith.presentation.utils.AzimuthProvider
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
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class CompassViewModelTests {
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var viewModel: CompassViewModel
    private lateinit var useCase: QiblahBearingCalculatorUseCase
    private lateinit var azimuthProvider: AzimuthProvider
    private lateinit var locationService: LocationService
    private lateinit var addressesRepository: AddressesRepository

    @BeforeTest
    fun setUp() {
        startKoin {
            modules(module { single { mock<SnackbarHandler>(MockMode.autofill) } })
        }
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        useCase = QiblahBearingCalculatorUseCase()
        azimuthProvider = mock(mode = MockMode.autofill)
        addressesRepository = mock(mode = MockMode.autofill)

        locationService = LocationService(addressesRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun `init should navigate to addresses screen when address is null`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns null

        createViewModel()

        viewModel.uiEffect.test {
            advanceUntilIdle()
            assertEquals(CompassEffect.NavigateToAddressesScreen, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init should not update city state when address has empty addressLine`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns addressWithEmptyLine

        createViewModel()
        advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.address)
    }

    @Test
    fun `init should update address state when address is valid`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns cairoAddress
        everySuspend { azimuthProvider.startListening() } returns singleAzimuthFlow

        createViewModel()
        advanceUntilIdle()

        assertEquals("Cairo, Egypt", viewModel.uiState.value.address)
    }

    @Test
    fun `onChangeLocation should navigate to addresses screen when city is not empty`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns cairoAddress
        everySuspend { azimuthProvider.startListening() } returns singleAzimuthFlow

        createViewModel()
        advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.onLocationClick()
            advanceUntilIdle()

            assertEquals(CompassEffect.NavigateToAddressesScreen, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onChangeLocation should navigate to addresses screen when city is empty`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns addressWithEmptyCity
        everySuspend { azimuthProvider.startListening() } returns emptyAzimuthFlow

        createViewModel()
        advanceUntilIdle()

        viewModel.uiEffect.test {
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.uiEffect.test {
            viewModel.onLocationClick()
            advanceUntilIdle()

            assertEquals(CompassEffect.NavigateToAddressesScreen, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `azimuth changes should calculate continuous azimuth correctly`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns cairoAddress
        everySuspend { azimuthProvider.startListening() } returns flowOf(45f)

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        val expectedContinuous = useCase.calculateContinuousAzimuth(45f)
        assertEquals(expectedContinuous, state.continuousAzimuth)
    }

    @Test
    fun `angle to qiblah should use shortest angle difference calculation`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns cairoAddress
        everySuspend { azimuthProvider.startListening() } returns flowOf(350f)

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        val qiblahAngle = state.qiblahAngleValue
        val expectedAngle = useCase.getShortestAngleDifference(350f, qiblahAngle)

        assertEquals(expectedAngle, state.angleToQiblah)
    }

    @Test
    fun `multiple azimuth changes should update state progressively`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns cairoAddress
        everySuspend { azimuthProvider.startListening() } returns flowOf(0f, 90f, 180f, 270f)

        createViewModel()
        advanceUntilIdle()

        val finalState = viewModel.uiState.value

        val expectedContinuous = useCase.calculateContinuousAzimuth(270f)
        assertEquals(expectedContinuous, finalState.continuousAzimuth)

        assertTrue(finalState.angleToQiblah != 0f || finalState.qiblahAngleValue == 270f)
    }

    @Test
    fun `init should handle repository errors gracefully`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } throws Exception("Repository error")

        createViewModel()

        viewModel.uiEffect.test {
            advanceUntilIdle()
            assertEquals(CompassEffect.NavigateToAddressesScreen, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        val state = viewModel.uiState.value
        assertEquals("", state.address)
        assertEquals(0f, state.qiblahAngleValue)
    }

    @Test
    fun `refreshAddress should reload location and update city`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns cairoAddress
        everySuspend { azimuthProvider.startListening() } returns singleAzimuthFlow

        createViewModel()
        advanceUntilIdle()

        assertEquals("Cairo, Egypt", viewModel.uiState.value.address)

        everySuspend { addressesRepository.getActiveAddress() } returns makkahAddress

        viewModel.refreshAddress()
        advanceUntilIdle()

        assertEquals("Makkah, Saudi Arabia", viewModel.uiState.value.address)
    }

    @Test
    fun `city and qiblah angle should remain unchanged during azimuth updates`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns cairoAddress
        everySuspend { azimuthProvider.startListening() } returns flowOf(0f, 45f, 90f)

        createViewModel()
        advanceUntilIdle()

        val initialCity = viewModel.uiState.value.address
        val initialQiblah = viewModel.uiState.value.qiblahAngleValue

        advanceUntilIdle()

        val finalState = viewModel.uiState.value
        assertEquals(initialCity, finalState.address)
        assertEquals(initialQiblah, finalState.qiblahAngleValue)
    }

    @Test
    fun `init should load qiblah angle after getting user location`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns cairoAddress
        everySuspend { azimuthProvider.startListening() } returns singleAzimuthFlow

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.qiblahAngleValue > 0f)
    }

    private fun createViewModel() {
        viewModel = CompassViewModel(
            bearingCalculatorUseCase = useCase,
            locationService = locationService,
            azimuthProvider = azimuthProvider,
            dispatcher = testDispatcher
        )
    }

    companion object TestData {
        private const val CAIRO_LATITUDE = 30.0594628
        private const val CAIRO_LONGITUDE = 31.1760627
        private const val CAIRO_FULL_NAME = "Cairo, Egypt"

        private const val MAKKAH_LATITUDE = 21.4225
        private const val MAKKAH_LONGITUDE = 39.8262
        private const val MAKKAH_FULL_NAME = "Makkah, Saudi Arabia"

        val cairoAddress = Address(
            id = Uuid.random(),
            latitude = CAIRO_LATITUDE,
            longitude = CAIRO_LONGITUDE,
            addressLine = CAIRO_FULL_NAME,
            addressType = AddressType.Home
        )

        val addressWithEmptyLine = Address(
            id = Uuid.random(),
            latitude = 30.0594628,
            longitude = 31.1760627,
            addressLine = "",
            addressType = AddressType.Home
        )

        val makkahAddress = Address(
            id = Uuid.random(),
            latitude = MAKKAH_LATITUDE,
            longitude = MAKKAH_LONGITUDE,
            addressLine = MAKKAH_FULL_NAME,
            addressType = AddressType.Home
        )

        val addressWithEmptyCity = Address(
            id = Uuid.random(),
            latitude = CAIRO_LATITUDE,
            longitude = CAIRO_LONGITUDE,
            addressLine = "",
            addressType = AddressType.Home
        )

        val emptyAzimuthFlow: Flow<Float> = flowOf()
        val singleAzimuthFlow: Flow<Float> = flowOf(0f)
    }
}