package net.thechance.mena.faith.presentation.feature.qiblah.compass

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify
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

        locationService = LocationService(addressesRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should navigate to identity screen when address is null`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns null

        createViewModel()

        viewModel.uiEffect.test {
            advanceUntilIdle()
            assertEquals(CompassEffect.NavigateToMyLocation, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init should not navigate when address has all required data even if id is null`() =
        runTest {
            everySuspend { addressesRepository.getActiveAddress() } returns cairoAddressWithoutId
            everySuspend { azimuthProvider.startListening() } returns singleAzimuthFlow

            createViewModel()
            advanceUntilIdle()

            viewModel.uiState.test {
                val state = awaitItem()
                assertEquals(CAIRO_CITY, state.city)
                assertTrue(state.qiblahAngleValue != 0f)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `qiblah angle should be calculated correctly for valid address`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns cairoAddress
        everySuspend { azimuthProvider.startListening() } returns singleAzimuthFlow

        createViewModel()
        advanceUntilIdle()

        val expectedAngle = useCase.calculateQiblahAngle(cairoAddress)
        assertEquals(expectedAngle.toFloat(), viewModel.uiState.value.qiblahAngleValue)
    }

    @Test
    fun `angle to qiblah should be calculated as shortest path`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns cairoAddress
        everySuspend { azimuthProvider.startListening() } returns multipleAzimuthFlow

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(
            state.angleToQiblah in ANGLE_RANGE,
            ANGLE_RANGE_MESSAGE
        )
    }

    @Test
    fun `initial state should have zero values`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns cairoAddress
        everySuspend { azimuthProvider.startListening() } returns emptyAzimuthFlow

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(INITIAL_AZIMUTH, state.continuousAzimuth)
        assertEquals(INITIAL_ANGLE_TO_QIBLAH, state.angleToQiblah)
    }

    @Test
    fun `onBackClick should emit NavigateBack effect`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns cairoAddress
        everySuspend { azimuthProvider.startListening() } returns emptyAzimuthFlow

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
        everySuspend { addressesRepository.getActiveAddress() } returns cairoAddress
        everySuspend { azimuthProvider.startListening() } returns singleAzimuthFlow

        createViewModel()
        advanceUntilIdle()

        val initialQiblah = viewModel.uiState.value.qiblahAngleValue
        val initialCity = viewModel.uiState.value.city
        assertEquals(CAIRO_FULL_NAME, initialCity)
        assertTrue(initialQiblah != 0f, INITIAL_QIBLAH_MESSAGE)

        everySuspend { addressesRepository.getActiveAddress() } returns makkahAddress

        viewModel.refreshAddress()
        advanceUntilIdle()

        val updatedQiblah = viewModel.uiState.value.qiblahAngleValue
        val updatedCity = viewModel.uiState.value.city

        assertNotEquals(initialQiblah, updatedQiblah, QIBLAH_RECALCULATED_MESSAGE)
        assertEquals(MAKKAH_FULL_NAME, updatedCity)
    }

    @Test
    fun `azimuth changes should update continuous azimuth and angle to qiblah`() = runTest {
        everySuspend { addressesRepository.getActiveAddress() } returns cairoAddress
        everySuspend { azimuthProvider.startListening() } returns variableAzimuthFlow

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.continuousAzimuth != 0f, AZIMUTH_UPDATED_MESSAGE)
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

    companion object TestData {

        private const val CAIRO_LATITUDE = 30.0594628
        private const val CAIRO_LONGITUDE = 31.1760627
        const val CAIRO_CITY = "Cairo"
        const val CAIRO_FULL_NAME = "Cairo, Egypt"

        private const val MAKKAH_LATITUDE = 21.4225
        private const val MAKKAH_LONGITUDE = 39.8262
        const val MAKKAH_FULL_NAME = "Makkah, Saudi Arabia"

        val cairoAddress = Address(
            id = Uuid.random(),
            latitude = CAIRO_LATITUDE,
            longitude = CAIRO_LONGITUDE,
            addressLine = CAIRO_FULL_NAME,
            addressType = AddressType.Home
        )

        val cairoAddressWithoutId = Address(
            id = null,
            latitude = CAIRO_LATITUDE,
            longitude = CAIRO_LONGITUDE,
            addressLine = CAIRO_CITY,
            addressType = AddressType.Home
        )

        val makkahAddress = Address(
            id = Uuid.random(),
            latitude = MAKKAH_LATITUDE,
            longitude = MAKKAH_LONGITUDE,
            addressLine = MAKKAH_FULL_NAME,
            addressType = AddressType.Home
        )

        val emptyAzimuthFlow: Flow<Float> = flowOf()
        val singleAzimuthFlow: Flow<Float> = flowOf(0f)
        val multipleAzimuthFlow: Flow<Float> = flowOf(0f, 45f, 90f)
        val variableAzimuthFlow: Flow<Float> = flowOf(0f, 45f, 90f, 180f)

        // Expected values
        const val INITIAL_AZIMUTH = 0f
        const val INITIAL_ANGLE_TO_QIBLAH = 0f
        val ANGLE_RANGE = -180f..180f

        const val ANGLE_RANGE_MESSAGE = "Angle to Qiblah should be in range -180 to 180"
        const val INITIAL_QIBLAH_MESSAGE = "Initial qiblah should be calculated"
        const val QIBLAH_RECALCULATED_MESSAGE = "Qiblah angle should be recalculated"
        const val AZIMUTH_UPDATED_MESSAGE = "Continuous azimuth should be updated"
    }
}