package net.thechance.mena.dukan.presentation.viewModel.createDukan

import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
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
        everySuspend { dukanRepository.getDukanStyles() } returns fakeDukanStyle()
        everySuspend { dukanRepository.getDukanColors() } returns fakeDukanColor()
        everySuspend { dukanRepository.getCategories() } returns fakeCategories()

        createDukanViewModel = CreateDukanViewModel(
            dukanRepository,
            locationRepository,
            testDispatcher
        )
    }

    @Test
    fun `init should load categories`() = runTest {
        createDukanViewModel.state.test {
            val state = awaitItem()
            assertEquals(fakeCategories().size, state.dukanCategories.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init should load colors`() = runTest {
        createDukanViewModel.state.test {
            val state = awaitItem()
            assertEquals(fakeDukanColor().size, state.dukanColors.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init should load styles`() = runTest {
        createDukanViewModel.state.test {
            val state = awaitItem()
            assertEquals(fakeDukanStyle().size, state.dukanStyles.size)
            cancelAndIgnoreRemainingEvents()
        }
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

    @Test
    fun ` when onColorClicked is called, then the ViewModel's state of selectedColor should be updated`() =
        runTest {

            val color = ColorUiState(
                id = "1",
                color = 0xFFF545
            )
            createDukanViewModel.onColorClicked(color)

            createDukanViewModel.state.test {
                val state = awaitItem()
                assertEquals(color, state.selectedColor)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `when onStyleClicked is called, then the ViewModel's state of selectedStyle should be updated`() =
        runTest {

            val style = Dukan.Style.WIDE_IMAGE

            createDukanViewModel.onStyleClicked(style)

            createDukanViewModel.state.test {
                val state = awaitItem()
                assertEquals(style, state.selectedStyle)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Given selectedStyle is not null and selectedColor is not null, when updateCreateButtonState is called, then isButtonEnabled should be true`() =
        runTest {
            val style = Dukan.Style.WIDE_IMAGE
            val color = ColorUiState(
                id = "1",
                color = 0xFFF545
            )

            createDukanViewModel.onStyleClicked(style)
            createDukanViewModel.onColorClicked(color)

            createDukanViewModel.updateCreateButtonState()

            createDukanViewModel.state.test {
                val state = awaitItem()
                assertEquals(true, state.isButtonEnabled)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Given selectedStyle is null and selectedColor is not null, when updateCreateButtonState is called, then isButtonEnabled should be false`() =
        runTest {

            val color = ColorUiState(
                id = "1",
                color = 0xFFF545
            )

            createDukanViewModel.onColorClicked(color)

            createDukanViewModel.updateCreateButtonState()

            createDukanViewModel.state.test {
                val state = awaitItem()
                assertEquals(false, state.isButtonEnabled)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Given selectedStyle is not null and selectedColor is null, when updateCreateButtonState is called, then isButtonEnabled should be false`() =
        runTest {
            val style = Dukan.Style.WIDE_IMAGE

            createDukanViewModel.onStyleClicked(style)

            createDukanViewModel.updateCreateButtonState()

            createDukanViewModel.state.test {
                val state = awaitItem()
                assertEquals(false, state.isButtonEnabled)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Given selectedStyle is null and selectedColor is null, when updateCreateButtonState is called, then isButtonEnabled should be false`() =
        runTest {
            createDukanViewModel.updateCreateButtonState()

            createDukanViewModel.state.test {
                val state = awaitItem()
                assertEquals(false, state.isButtonEnabled)
                cancelAndIgnoreRemainingEvents()
            }
        }
}

private fun fakeDukanColor(): List<Color> {
    return listOf(
        Color(
            id = "1",
            hexCode = "#F77053"
        ),
        Color(
            id = "2",
            hexCode = "#F4C343"
        ),
        Color(
            id = "3",
            hexCode = "#C30C30"
        ),
        Color(
            id = "4",
            hexCode = "#30ABE8"
        ),
    )
}


private fun fakeDukanStyle(): List<Dukan.Style> {
    return listOf(
        Dukan.Style.WIDE_IMAGE,
        Dukan.Style.SMALL_IMAGE,
        Dukan.Style.NO_IMAGE
    )
}

private fun fakeCategories(): List<Category> {
    return listOf(
        Category(
            id = "1",
            name = "Electronics",
            imageUrl = "https://example.com/electronics.png"
        ),
        Category(
            id = "2",
            name = "Clothes",
            imageUrl = "https://example.com/clothes.png"
        ),
        Category(
            id = "3",
            name = "Groceries",
            imageUrl = "https://example.com/groceries.png"
        ),
        Category(
            id = "4",
            name = "Books",
            imageUrl = "https://example.com/books.png"
        )
    )
}
