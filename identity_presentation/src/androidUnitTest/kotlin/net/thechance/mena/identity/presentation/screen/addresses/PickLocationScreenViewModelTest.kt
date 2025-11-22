package net.thechance.mena.identity.presentation.screen.addresses

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isInstanceOf
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.identity.domain.exception.UnableToFindLocationException
import net.thechance.mena.identity.domain.repository.AddressesRepository
import net.thechance.mena.identity.presentation.screen.addresses.pickLocation.PickLocationScreenUIEffect
import net.thechance.mena.identity.presentation.screen.addresses.pickLocation.PickLocationScreenViewModel
import net.thechance.mena.identity.presentation.screen.addresses.shared.CoordinatesUiState
import net.thechance.mena.identity.presentation.screen.addresses.shared.toCoordinatesUiState
import net.thechance.mena.identity.presentation.screen.addresses.shared.toEntity
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionHandler
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import org.maplibre.compose.camera.CameraPosition
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class PickLocationScreenViewModelTest {
    private val mobileLocationRepository = mockk<AddressesRepository>()
    private val locationPermissionHandler = mockk<PermissionHandler>()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: PickLocationScreenViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = PickLocationScreenViewModel(
            mobileLocationRepository,
            testDispatcher,
            locationPermissionHandler,
            null
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onClickMap should update state with coordinates and location name`() = runTest {
        val coordinates = CoordinatesUiState(28.0, 29.0)
        val address = "Test Address"
        coEvery { mobileLocationRepository.getLocationName(coordinates.toEntity()) } returns address

        viewModel.onClickMap(coordinates)
        testDispatcher.scheduler.advanceUntilIdle()


        assert(viewModel.state.value.currentLocation == coordinates)
        assert(viewModel.state.value.address == address)
    }

    @Test
    fun `onClickGps should update state with current location`() = runTest {
        val coordinates = CoordinatesUiState(28.0, 29.0)
        coEvery { mobileLocationRepository.getCurrentLocation() } returns coordinates.toEntity()

        viewModel.onClickGps()
        testDispatcher.scheduler.advanceUntilIdle()

        assert(viewModel.state.value.currentLocation == coordinates)
        assert(viewModel.state.value.animateToCurrentLocation)
        assert(!viewModel.state.value.isGpsButtonLoading)
    }

    @Test
    fun `onClickConfirm should send NavigateToAddLocation effect`() = runTest {
        val coordinates = CoordinatesUiState(28.0, 29.0)
        val address = "Test Address"
        coEvery { mobileLocationRepository.getLocationName(coordinates.toEntity()) } returns address


        viewModel.effect.test {
            viewModel.onClickMap(coordinates)
            viewModel.onClickConfirm()
            testDispatcher.scheduler.advanceUntilIdle()

            val emittedEffect = awaitItem()
            assert(emittedEffect is PickLocationScreenUIEffect.NavigateBackWithLocation)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `onCameraMoved should update state with camera position`() = runTest {
        val cameraPosition = CameraPosition()
        viewModel.onMoveCamera(cameraPosition.target.toCoordinatesUiState())
        testDispatcher.scheduler.advanceUntilIdle()

        assert(viewModel.state.value.currentLocation == cameraPosition.target.toCoordinatesUiState())
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
    fun `onClickGps should show snackbar with error message when locationPermissionHandler throws`() =
        runTest {
            coEvery { mobileLocationRepository.getCurrentLocation() } throws Exception()
            coEvery { locationPermissionHandler.checkPermission() } throws Exception()

            viewModel.onClickGps()

            viewModel.effect.test {
                testDispatcher.scheduler.advanceUntilIdle()
                assertThat(awaitItem()).isInstanceOf(PickLocationScreenUIEffect.ShowSnackBarError::class)
            }

            assert(!viewModel.state.value.isGpsButtonLoading)
        }

    @Test
    fun `onClickGps should update state with error message and navigate to enable location when location repository throws UnableToFindLocationException`() =
        runTest {
            coEvery { mobileLocationRepository.getCurrentLocation() } throws UnableToFindLocationException()
            coEvery { locationPermissionHandler.checkPermission() } returns PermissionState.DENIED

            viewModel.onClickGps()

            viewModel.effect.test {
                testDispatcher.scheduler.advanceUntilIdle()
                assertThat(awaitItem()).isInstanceOf(PickLocationScreenUIEffect.NavigateToEnableLocation::class)
            }
        }
}