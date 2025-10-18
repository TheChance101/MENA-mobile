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
import net.thechance.mena.faith.domain.entity.Location
import net.thechance.mena.faith.domain.usecase.QiblahBearingCalculatorUseCase
import net.thechance.mena.faith.presentation.util.AzimuthProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CompassViewModelTest {
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var viewModel: CompassViewModel
    private lateinit var useCase: QiblahBearingCalculatorUseCase
    private lateinit var azimuthProvider: AzimuthProvider

    @BeforeTest
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        useCase = QiblahBearingCalculatorUseCase()
        azimuthProvider = mock(mode = MockMode.autofill)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should calculate qiblah angle and start listening to azimuth`() = runTest {
        // Given
        val azimuthFlow = flowOf(45f, 90f, 135f)
        everySuspend { azimuthProvider.startListening() } returns azimuthFlow

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.qiblahAngleValue > 0f, "Qiblah angle should be calculated")
        verify { azimuthProvider.startListening() }
    }

    @Test
    fun `qiblah angle should be calculated correctly for default location`() = runTest {
        // Given
        val azimuthFlow = flowOf(0f)
        everySuspend { azimuthProvider.startListening() } returns azimuthFlow

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        val expectedAngle = useCase.calculateQiblahAngle(Location(longitude = 31.1760627, latitude = 30.0594628))
        assertEquals(expectedAngle.toFloat(), viewModel.uiState.value.qiblahAngleValue)
    }

    @Test
    fun `angle to qiblah should be calculated as shortest path`() = runTest {
        // Given
        val azimuthFlow = flowOf(0f)
        everySuspend { azimuthProvider.startListening() } returns azimuthFlow

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(
            state.angleToQiblah in -180f..180f,
            "Angle to Qiblah should be in range -180 to 180"
        )
    }

    @Test
    fun `initial state should have zero values`() = runTest {
        // Given
        val azimuthFlow = flowOf<Float>()
        everySuspend { azimuthProvider.startListening() } returns azimuthFlow

        // When
        createViewModel()

        // Then
        val state = viewModel.uiState.value
        assertEquals(0f, state.continuousAzimuth)
        assertEquals(0f, state.angleToQiblah)
    }

    @Test
    fun `onBackClick should emit NavigateBack effect`() = runTest {
        // Given
        everySuspend { azimuthProvider.startListening() } returns flowOf()

        createViewModel()
        advanceUntilIdle()

        // When & Then
        viewModel.uiEffect.test {
            viewModel.onBackClick()
            advanceUntilIdle()
            assertEquals(CompassEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createViewModel() {
        viewModel = CompassViewModel(
            bearingCalculatorUseCase = useCase,
            azimuthProvider = azimuthProvider,
            dispatcher = testDispatcher
        )
    }
}
