package net.thechance.mena.faith.presentation.feature.qiblah.compass

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.faith.domain.usecase.QiblahBearingCalculatorUseCase
import net.thechance.mena.faith.presentation.utils.AzimuthProvider
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.domain.repository.AddressesRepository
import net.thechance.mena.identity.domain.service.LocationService
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class CompassViewModelTest {
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var viewModel: CompassViewModel
    private lateinit var useCase: QiblahBearingCalculatorUseCase
    private lateinit var azimuthProvider: AzimuthProvider
    private lateinit var locationService: LocationService
    private lateinit var addressesRepository: AddressesRepository


    @BeforeTest
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        useCase = QiblahBearingCalculatorUseCase()
        azimuthProvider = mock(mode = MockMode.autofill)
        addressesRepository = mock(mode = MockMode.autofill)
        locationService = mock(mode = MockMode.autofill)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should calculate qiblah angle and start listening to azimuth when address is valid`() =
        runTest {
            val validAddress = createValidAddress()
        val azimuthFlow = flowOf(45f, 90f, 135f)
            everySuspend { locationService.getActiveAddress() } returns validAddress
        everySuspend { azimuthProvider.startListening() } returns azimuthFlow

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.qiblahAngleValue > 0f, "Qiblah angle should be calculated")
            assertEquals("Cairo, Egypt", state.city)
        verify { azimuthProvider.startListening() }
    }

    @Test
    fun `init should navigate to identity screen when address is null`() = runTest {
        everySuspend { locationService.getActiveAddress() } returns null

        viewModel.uiEffect.test {
            createViewModel()
            advanceUntilIdle()
            assertEquals(CompassEffect.NavigateToMyLocation, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init should navigate to identity screen when address id is null`() = runTest {
        val addressWithoutId = Address(
            id = null,
            latitude = 30.0594628,
            longitude = 31.1760627,
            addressLine = "Cairo",
            addressType = AddressType.Home
        )
        everySuspend { locationService.getActiveAddress() } returns addressWithoutId

        viewModel.uiEffect.test {
            createViewModel()
            advanceUntilIdle()
            assertEquals(CompassEffect.NavigateToMyLocation, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `qiblah angle should be calculated correctly for valid address`() = runTest {
        val validAddress = createValidAddress()
        val azimuthFlow = flowOf(0f)
        everySuspend { locationService.getActiveAddress() } returns validAddress
        everySuspend { azimuthProvider.startListening() } returns azimuthFlow

        createViewModel()
        advanceUntilIdle()

        val expectedAngle = useCase.calculateQiblahAngle(validAddress)
        assertEquals(expectedAngle.toFloat(), viewModel.uiState.value.qiblahAngleValue)
    }

    @Test
    fun `angle to qiblah should be calculated as shortest path`() = runTest {
        val validAddress = createValidAddress()
        val azimuthFlow = flowOf(0f, 45f, 90f)
        everySuspend { locationService.getActiveAddress() } returns validAddress
        everySuspend { azimuthProvider.startListening() } returns azimuthFlow

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(
            state.angleToQiblah in -180f..180f,
            "Angle to Qiblah should be in range -180 to 180"
        )
    }

    @Test
    fun `initial state should have zero values`() = runTest {
        val validAddress = createValidAddress()
        everySuspend { locationService.getActiveAddress() } returns validAddress
        everySuspend { azimuthProvider.startListening() } returns flowOf()

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(0f, state.continuousAzimuth)
        assertEquals(0f, state.angleToQiblah)
    }

    @Test
    fun `onBackClick should emit NavigateBack effect`() = runTest {
        val validAddress = createValidAddress()
        everySuspend { locationService.getActiveAddress() } returns validAddress
        everySuspend { azimuthProvider.startListening() } returns flowOf()

        createViewModel()
        advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.onBackClick()
            advanceUntilIdle()
            assertEquals(CompassEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `refreshAddress should reload compass data and recalculate qiblah`() = runTest {
        // Given
        val updatedAddress = createValidAddress(
            latitude = 21.4225,
            longitude = 39.8262,
            addressLine = "Makkah"
        )
        everySuspend { locationService.getActiveAddress() } returns updatedAddress
        everySuspend { azimuthProvider.startListening() } returns flowOf(0f)

        createViewModel()
        advanceUntilIdle()

        val initialQiblah = viewModel.uiState.value.qiblahAngleValue

        viewModel.refreshAddress()
        advanceUntilIdle()

        val updatedQiblah = viewModel.uiState.value.qiblahAngleValue
        assertNotEquals(initialQiblah, updatedQiblah, "Qiblah angle should be recalculated")
        assertEquals("Makkah", viewModel.uiState.value.city)
    }

    @Test
    fun `azimuth changes should update continuous azimuth and angle to qiblah`() = runTest {
        val validAddress = createValidAddress()
        val azimuthFlow = flowOf(0f, 45f, 90f, 180f)
        everySuspend { locationService.getActiveAddress() } returns validAddress
        everySuspend { azimuthProvider.startListening() } returns azimuthFlow

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.continuousAzimuth != 0f, "Continuous azimuth should be updated")
        verify { azimuthProvider.startListening() }
    }

    private fun createViewModel() {
        viewModel = CompassViewModel(
            bearingCalculatorUseCase = useCase,
            locationService = locationService,
            azimuthProvider = azimuthProvider,
            dispatcher = testDispatcher
        )
    }

    private fun createValidAddress(
        latitude: Double = 30.0594628,
        longitude: Double = 31.1760627,
        addressLine: String = "Cairo, Egypt"
    ): Address {
        return Address(
            id = Uuid.random(),
            latitude = latitude,
            longitude = longitude,
            addressLine = addressLine,
            addressType = AddressType.Home
        )
    }
}