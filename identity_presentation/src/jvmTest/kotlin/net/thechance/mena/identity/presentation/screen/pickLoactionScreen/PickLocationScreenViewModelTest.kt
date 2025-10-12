package net.thechance.mena.identity.presentation.screen.pickLoactionScreen

import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.identity.domain.exception.UnableToFindLocationException
import net.thechance.mena.identity.domain.repository.LocationRepository
import net.thechance.mena.identity.presentation.screen.pickLocation.PickLocationScreenUIEffect
import net.thechance.mena.identity.presentation.screen.pickLocation.PickLocationScreenUIState
import net.thechance.mena.identity.presentation.screen.pickLocation.PickLocationScreenViewModel
import net.thechance.mena.identity.presentation.screen.pickLocation.toEntity
import org.maplibre.compose.camera.CameraPosition
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class PickLocationScreenViewModelTest {
    private val locationRepository = mockk<LocationRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: PickLocationScreenViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = PickLocationScreenViewModel(locationRepository, testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onClickMap should update state with coordinates and location name`() = runTest {
        val coordinates = PickLocationScreenUIState.CoordinatesUiState(28.0, 29.0)
        val pointerLocation = DpOffset(10.0.dp, 20.0.dp)
        val address = "Test Address"
        coEvery { locationRepository.getLocationName(coordinates.toEntity()) } returns address

        viewModel.onClickMap(coordinates, pointerLocation)
        testDispatcher.scheduler.advanceUntilIdle()


        assert(viewModel.state.value.currentLocation == coordinates)
        assert(viewModel.state.value.pointerLocation == pointerLocation)
        assert(viewModel.state.value.address == address)
        assert(viewModel.state.value.isMapLocked)
    }

    @Test
    fun `onClickGps should update state with current location`() = runTest {
        val coordinates = PickLocationScreenUIState.CoordinatesUiState(28.0, 29.0)
        coEvery { locationRepository.getCurrentLocation() } returns coordinates.toEntity()

        viewModel.onClickGps()
        testDispatcher.scheduler.advanceUntilIdle()

        assert(viewModel.state.value.currentLocation == coordinates)
        assert(viewModel.state.value.isMapLocked)
        assert(viewModel.state.value.animateToCurrentLocation)
        assert(!viewModel.state.value.isGpsButtonLoading)
    }

    @Test
    fun `onClickConfirm should send NavigateToAddLocation effect`() = runTest {
        val coordinates = PickLocationScreenUIState.CoordinatesUiState(28.0, 29.0)
        val address = "Test Address"
        coEvery { locationRepository.getLocationName(coordinates.toEntity()) } returns address


        viewModel.effect.test {
            viewModel.onClickMap(coordinates, DpOffset(10.0.dp, 20.0.dp))
            viewModel.onClickConfirm()
            testDispatcher.scheduler.advanceUntilIdle()

            val emittedEffect = awaitItem()
            assert(emittedEffect is PickLocationScreenUIEffect.NavigateToAddLocation)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `onCameraMoved should update state with camera position`() = runTest {
        val cameraPosition = CameraPosition()
        viewModel.onCameraMoved(cameraPosition)
        testDispatcher.scheduler.advanceUntilIdle()

        assert(viewModel.state.value.cameraPosition == cameraPosition)
        assert(!viewModel.state.value.animateToCurrentLocation)
    }

    @Test
    fun `onClickBack should send NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onClickBack()
            val emittedEffect = awaitItem()
            assert(emittedEffect is PickLocationScreenUIEffect.NavigateBack)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `onClickGps should update state with error message when location repository throws`() {
        coEvery { locationRepository.getCurrentLocation() } throws Exception()

        viewModel.onClickGps()
        testDispatcher.scheduler.advanceUntilIdle()

        assert(viewModel.state.value.errorMessage != null)
        assert(!viewModel.state.value.isGpsButtonLoading)
    }

    @Test
    fun `onClickGps should update state with error message and navigate to enable location when location repository throws UnableToFindLocationException`() =
        runTest {
            coEvery { locationRepository.getCurrentLocation() } throws UnableToFindLocationException()
            viewModel.effect.test {
                viewModel.onClickGps()
                testDispatcher.scheduler.advanceUntilIdle()
                val emittedEffect = awaitItem()
                assert(emittedEffect is PickLocationScreenUIEffect.NavigateToEnableLocation)
                cancelAndConsumeRemainingEvents()
            }
        }

}