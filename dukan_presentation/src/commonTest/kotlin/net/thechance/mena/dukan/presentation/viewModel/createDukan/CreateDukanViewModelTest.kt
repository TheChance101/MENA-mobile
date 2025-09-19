package net.thechance.mena.dukan.presentation.viewModel.createDukan

import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import dev.mokkery.MockMode
import dev.mokkery.answering.calls
import dev.mokkery.everySuspend
import dev.mokkery.mock
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.dukan.domain.repository.DukanRepository
import net.thechance.mena.dukan.domain.repository.LocationRepository
import org.maplibre.compose.camera.CameraPosition
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class CreateDukanViewModelTest {

    private val locationRepository = mock<LocationRepository>(mode = MockMode.autofill)
    private val dukanRepository = mock<DukanRepository>(mode = MockMode.autofill)
    private lateinit var createDukanViewModel: CreateDukanViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        createDukanViewModel = CreateDukanViewModel(
            dukanRepository,
            locationRepository,
            testDispatcher
        )
    }

    @Test
    fun `onAddressChanged SHOULD update address state`() = runTest {
        val address = "This new address"

        createDukanViewModel.onAddressChanged(address)
        val newAddress = createDukanViewModel.state.value.address
        assertEquals(newAddress, address)
    }

    @Test
    fun `onAddressChanged SHOULD set isButtonEnabled state to false if address is blank`() =
        runTest {
            val address = ""

            createDukanViewModel.onAddressChanged(address)
            val buttonState = createDukanViewModel.state.value.isButtonEnabled
            assertFalse(buttonState)
        }

    @Test
    fun `onMapClicked SHOULD return the address of the selected location`() = runTest {
        // Given
        val expectedAddress = "Egypt"
        val selectedCoordinates = CreateDukanUiState.CoordinatesUiState(28.0, 29.0)


        val selectedPointerLocation = DpOffset(2.dp, 4.dp)
        everySuspend {
            locationRepository.getCurrentLocationName(selectedCoordinates.toEntity())
        } calls { expectedAddress }

        // when
        createDukanViewModel.onMapClicked(selectedCoordinates, selectedPointerLocation)

        testDispatcher.scheduler.advanceUntilIdle()
        val resultingAddress = createDukanViewModel.state.value.address

        // Then
        assertEquals(resultingAddress, expectedAddress)
    }

    @Test
    fun `onMapClicked SHOULD return set the coordinates of selected location`() = runTest {
        // Given
        val expectedAddress = "Egypt"
        val selectedCoordinates = CreateDukanUiState.CoordinatesUiState(28.0, 29.0)

        val selectedPointerLocation = DpOffset(2.dp, 4.dp)
        everySuspend {
            locationRepository.getCurrentLocationName(
                selectedCoordinates.toEntity()
            )
        } calls { expectedAddress }

        // when
        createDukanViewModel.onMapClicked(selectedCoordinates, selectedPointerLocation)

        testDispatcher.scheduler.advanceUntilIdle()
        val resultingCoordinates = createDukanViewModel.state.value.currentLocation

        // Then
        assertEquals(resultingCoordinates, selectedCoordinates)
    }

    @Test
    fun `onMapClicked SHOULD return set the coordinates location inside the composable of selected location`() =
        runTest {
            // Given
            val expectedAddress = "Egypt"
            val selectedCoordinates = CreateDukanUiState.CoordinatesUiState(28.0, 29.0)

            val selectedPointerLocation = DpOffset(2.dp, 4.dp)
            everySuspend {
                locationRepository.getCurrentLocationName(
                    selectedCoordinates.toEntity()
                )
            } calls { expectedAddress }

            // when
            createDukanViewModel.onMapClicked(selectedCoordinates, selectedPointerLocation)

            testDispatcher.scheduler.advanceUntilIdle()
            val resultingPointerLocation = createDukanViewModel.state.value.pointerLocation

            // Then
            assertEquals(resultingPointerLocation, selectedPointerLocation)
        }

    @Test
    fun `onEditMapLocationClicked SHOULD reset the address`() = runTest {
        // Given
        val startingAddress = "Egypt"
        val startingCoordinates = CreateDukanUiState.CoordinatesUiState(28.0, 29.0)

        val startingPointerLocation = DpOffset(2.dp, 4.dp)
        everySuspend {
            locationRepository.getCurrentLocationName(
                startingCoordinates.toEntity()
            )
        } calls { startingAddress }

        // when
        createDukanViewModel.onMapClicked(startingCoordinates, startingPointerLocation)

        createDukanViewModel.onEditMapLocationClicked()

        // Then
        val resultingAddress = createDukanViewModel.state.value.address
        assertEquals("", resultingAddress)
    }

    @Test
    fun `onEditMapLocationClicked SHOULD current location`() = runTest {
        // Given
        val startingAddress = "Egypt"
        val startingCoordinates = CreateDukanUiState.CoordinatesUiState(28.0, 29.0)

        val startingPointerLocation = DpOffset(2.dp, 4.dp)
        everySuspend {
            locationRepository.getCurrentLocationName(
                startingCoordinates.toEntity()
            )
        } calls { startingAddress }

        // when
        createDukanViewModel.onMapClicked(startingCoordinates, startingPointerLocation)

        createDukanViewModel.onEditMapLocationClicked()

        // Then
        val resultingLocation = createDukanViewModel.state.value.currentLocation
        assertEquals(CreateDukanUiState.CoordinatesUiState(), resultingLocation)
    }

    @Test
    fun `onEditMapLocationClicked SHOULD pointerLocation`() = runTest {
        // Given
        val startingAddress = "Egypt"
        val startingCoordinates = CreateDukanUiState.CoordinatesUiState(28.0, 29.0)

        val startingPointerLocation = DpOffset(2.dp, 4.dp)
        everySuspend {
            locationRepository.getCurrentLocationName(
                startingCoordinates.toEntity()
            )
        } calls { startingAddress }

        // when
        createDukanViewModel.onMapClicked(startingCoordinates, startingPointerLocation)

        createDukanViewModel.onEditMapLocationClicked()

        // Then
        val resultingPointerLocation = createDukanViewModel.state.value.pointerLocation
        assertEquals(null, resultingPointerLocation)
    }

    @Test
    fun `onEditMapLocationClicked SHOULD disable the button`() = runTest {
        // Given
        val startingAddress = "Egypt"
        val startingCoordinates = CreateDukanUiState.CoordinatesUiState(28.0, 29.0)

        val startingPointerLocation = DpOffset(2.dp, 4.dp)
        everySuspend {
            locationRepository.getCurrentLocationName(
                startingCoordinates.toEntity()
            )
        } calls { startingAddress }

        // when
        createDukanViewModel.onMapClicked(startingCoordinates, startingPointerLocation)

        createDukanViewModel.onEditMapLocationClicked()

        // Then
        val resultingButtonState = createDukanViewModel.state.value.isButtonEnabled
        assertFalse(resultingButtonState)
    }

    @Test
    fun `onCameraMoved SHOULD update the cameraPosition`() = runTest {
        val expectedCameraPosition = CameraPosition(target = Position(29.0, 28.0))

        createDukanViewModel.onCameraMoved(expectedCameraPosition)

        val resultingCameraPosition = createDukanViewModel.state.value.cameraPosition

        assertEquals(expectedCameraPosition, resultingCameraPosition)
    }
}