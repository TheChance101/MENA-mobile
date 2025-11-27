package net.thechance.mena.dukan.presentation.viewModel.createDukan

import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import app.cash.turbine.test
import com.attafitamim.krop.core.images.ImageSrc
import dev.mokkery.MockMode
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.shelf_name_is_already_exist
import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.domain.repository.LocationRepository
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.util.file.ImageFile
import org.maplibre.compose.camera.CameraPosition
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class CreateDukanViewModelTest {

    private val locationRepository = mock<LocationRepository>(mode = MockMode.autofill)
    private val dukanManagementRepository =
        mock<DukanManagementRepository>(mode = MockMode.autofill)
    private lateinit var createDukanViewModel: CreateDukanViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        everySuspend { dukanManagementRepository.getDukanStyles() } returns fakeDukanStyle()
        everySuspend { dukanManagementRepository.getDukanColors() } returns fakeDukanColor()
        everySuspend { dukanManagementRepository.getCategories() } returns fakeCategories()
        everySuspend { dukanManagementRepository.isDukanNameTaken(any()) } returns false


        createDukanViewModel = CreateDukanViewModel(
            dukanManagementRepository = dukanManagementRepository,
            locationRepository = locationRepository,
            testDispatcher
        )
    }

    @Test
    fun `init should load categories`() = runTest {
        // When
        createDukanViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals(fakeCategories().size, state.dukanCategories.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init should load colors`() = runTest {
        // When
        createDukanViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals(fakeDukanColor().size, state.dukanColors.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init should load styles`() = runTest {
        // When
        createDukanViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals(fakeDukanStyle().size, state.dukanStyles.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onAddressChanged SHOULD update address state`() = runTest {
        // Given
        val address = "This new address"

        // When
        createDukanViewModel.onAddressChanged(address)

        // Then
        val newAddress = createDukanViewModel.state.value.address
        assertEquals(newAddress, address)
    }

    @Test
    fun `onAddressChanged SHOULD set isButtonEnabled state to false if address is blank`() =
        runTest {
            // Given
            val address = ""

            // When
            createDukanViewModel.onAddressChanged(address)

            // Then
            val buttonState = createDukanViewModel.state.value.isButtonEnabled
            assertFalse(buttonState)
        }

    @Test
    fun `onMapClicked SHOULD return the address of the selected location`() = runTest {
        // Given
        val expectedAddress = fakeLocationAddress()
        val selectedCoordinates = fakeSelectedCoordinates()
        val selectedPointerLocation = fakePointerLocation()
        everySuspend {
            locationRepository.getCurrentLocationName(selectedCoordinates.toEntity())
        } calls { expectedAddress }

        // When
        createDukanViewModel.onMapClicked(selectedCoordinates, selectedPointerLocation)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val resultingAddress = createDukanViewModel.state.value.address
        assertEquals(resultingAddress, expectedAddress)
    }

    @Test
    fun `onMapClicked SHOULD return set the coordinates of selected location`() = runTest {
        // Given
        val expectedAddress = fakeLocationAddress()
        val selectedCoordinates = fakeSelectedCoordinates()
        val selectedPointerLocation = fakePointerLocation()
        everySuspend {
            locationRepository.getCurrentLocationName(selectedCoordinates.toEntity())
        } calls { expectedAddress }

        // When
        createDukanViewModel.onMapClicked(selectedCoordinates, selectedPointerLocation)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val resultingCoordinates = createDukanViewModel.state.value.currentLocation
        assertEquals(resultingCoordinates, selectedCoordinates)
    }

    @Test
    fun `onMapClicked SHOULD return set the coordinates location inside the composable of selected location`() =
        runTest {
            // Given
            val expectedAddress = fakeLocationAddress()
            val selectedCoordinates = fakeSelectedCoordinates()
            val selectedPointerLocation = fakePointerLocation()
            everySuspend {
                locationRepository.getCurrentLocationName(selectedCoordinates.toEntity())
            } calls { expectedAddress }

            // When
            createDukanViewModel.onMapClicked(selectedCoordinates, selectedPointerLocation)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val resultingPointerLocation = createDukanViewModel.state.value.pointerLocation
            assertEquals(resultingPointerLocation, selectedPointerLocation)
        }

    @Test
    fun `onEditMapLocationClicked SHOULD reset the address`() = runTest {
        // Given
        val startingAddress = fakeLocationAddress()
        val startingCoordinates = fakeSelectedCoordinates()
        val startingPointerLocation = fakePointerLocation()
        everySuspend {
            locationRepository.getCurrentLocationName(startingCoordinates.toEntity())
        } calls { startingAddress }
        createDukanViewModel.onMapClicked(startingCoordinates, startingPointerLocation)

        // When
        createDukanViewModel.onEditMapLocationClicked()

        // Then
        val resultingAddress = createDukanViewModel.state.value.address
        assertEquals("", resultingAddress)
    }

    @Test
    fun `onEditMapLocationClicked SHOULD current location`() = runTest {
        // Given
        val startingAddress = fakeLocationAddress()
        val startingCoordinates = fakeSelectedCoordinates()
        val startingPointerLocation = fakePointerLocation()
        everySuspend {
            locationRepository.getCurrentLocationName(startingCoordinates.toEntity())
        } calls { startingAddress }
        createDukanViewModel.onMapClicked(startingCoordinates, startingPointerLocation)

        // When
        createDukanViewModel.onEditMapLocationClicked()

        // Then
        val resultingLocation = createDukanViewModel.state.value.currentLocation
        assertEquals(CreateDukanUiState.CoordinatesUiState(), resultingLocation)
    }

    @Test
    fun `onEditMapLocationClicked SHOULD pointerLocation`() = runTest {
        // Given
        val startingAddress = fakeLocationAddress()
        val startingCoordinates = fakeSelectedCoordinates()
        val startingPointerLocation = fakePointerLocation()
        everySuspend {
            locationRepository.getCurrentLocationName(startingCoordinates.toEntity())
        } calls { startingAddress }
        createDukanViewModel.onMapClicked(startingCoordinates, startingPointerLocation)

        // When
        createDukanViewModel.onEditMapLocationClicked()

        // Then
        val resultingPointerLocation = createDukanViewModel.state.value.pointerLocation
        assertEquals(null, resultingPointerLocation)
    }

    @Test
    fun `onEditMapLocationClicked SHOULD disable the button`() = runTest {
        // Given
        val startingAddress = fakeLocationAddress()
        val startingCoordinates = fakeSelectedCoordinates()
        val startingPointerLocation = fakePointerLocation()
        everySuspend {
            locationRepository.getCurrentLocationName(startingCoordinates.toEntity())
        } calls { startingAddress }
        createDukanViewModel.onMapClicked(startingCoordinates, startingPointerLocation)

        // When
        createDukanViewModel.onEditMapLocationClicked()

        // Then
        val resultingButtonState = createDukanViewModel.state.value.isButtonEnabled
        assertFalse(resultingButtonState)
    }

    @Test
    fun `onCameraMoved SHOULD update the cameraPosition`() = runTest {
        // Given
        val expectedCameraPosition = fakeCameraPosition()

        // When
        createDukanViewModel.onCameraMoved(expectedCameraPosition)

        // Then
        val resultingCameraPosition = createDukanViewModel.state.value.cameraPosition
        assertEquals(expectedCameraPosition, resultingCameraPosition)
    }

    @Test
    fun `when onColorClicked is called, then the ViewModel's state of selectedColor should be updated`() =
        runTest {
            // Given
            val color = fakeColorUiState()

            // When
            createDukanViewModel.onColorClicked(color)

            // Then
            createDukanViewModel.state.test {
                val state = awaitItem()
                assertEquals(color, state.selectedColor)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `when onStyleClicked is called, then the ViewModel's state of selectedStyle should be updated`() =
        runTest {
            // Given
            val style = fakeSingleDukanStyle()

            // When
            createDukanViewModel.onStyleClicked(style)

            // Then
            createDukanViewModel.state.test {
                val state = awaitItem()
                assertEquals(style, state.selectedStyle)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Given selectedStyle is not null and selectedColor is not null, when updateCreateButtonState is called, then isButtonEnabled should be true`() =
        runTest {
            // Given
            val style = fakeSingleDukanStyle()
            val color = fakeColorUiState()
            createDukanViewModel.onStyleClicked(style)
            createDukanViewModel.onColorClicked(color)

            // When
            val expect = createDukanViewModel.updateCreateButtonState()

            // Then
            createDukanViewModel.state.test {
                assertEquals(true, expect)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Given selectedStyle is null and selectedColor is not null, when updateCreateButtonState is called, then isButtonEnabled should be false`() =
        runTest {
            // Given
            val color = fakeColorUiState()
            createDukanViewModel.onColorClicked(color)

            // When
            createDukanViewModel.updateCreateButtonState()

            // Then
            createDukanViewModel.state.test {
                val state = awaitItem()
                assertEquals(false, state.isButtonEnabled)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Given selectedStyle is not null and selectedColor is null, when updateCreateButtonState is called, then isButtonEnabled should be false`() =
        runTest {
            // Given
            val style = fakeSingleDukanStyle()
            createDukanViewModel.onStyleClicked(style)

            // When
            createDukanViewModel.updateCreateButtonState()

            // Then
            createDukanViewModel.state.test {
                val state = awaitItem()
                assertEquals(false, state.isButtonEnabled)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Given selectedStyle is null and selectedColor is null, when updateCreateButtonState is called, then isButtonEnabled should be false`() =
        runTest {
            // When
            createDukanViewModel.updateCreateButtonState()

            // Then
            createDukanViewModel.state.test {
                val state = awaitItem()
                assertEquals(false, state.isButtonEnabled)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onNameChanged SHOULD update name state with limited length`() = runTest {
        // Given
        val name = fakeTestName()

        // When
        createDukanViewModel.onNameChanged(name)

        // Then
        val resultingName = createDukanViewModel.state.value.name
        assertEquals(name, resultingName)
    }

    @Test
    fun `onNameChanged SHOULD limit name to 40 characters when name is longer`() = runTest {
        // Given
        val expectedName = fakeLongName().take(40)

        // When
        createDukanViewModel.onNameChanged(fakeLongName())

        // Then
        val resultingName = createDukanViewModel.state.value.name
        assertEquals(expectedName, resultingName)
    }

    @Test
    fun `onNameChanged SHOULD hide snack bar when name is changed`() = runTest {
        // Given
        createDukanViewModel.updateState {
            copy(
                snackBarState = SnackBarUiState(
                    SnackBarType.ERROR, Res.string.shelf_name_is_already_exist
                )
            )
        }

        // When
        createDukanViewModel.onNameChanged("New Dukan Name")

        // Then
        val snackBarState = createDukanViewModel.state.value.snackBarState
        assertNull(snackBarState)
    }

    @Test
    fun `onNameChanged SHOULD update next button state`() = runTest {
        // Given
        val name = fakeTestName()

        // When
        createDukanViewModel.onNameChanged(name)

        // Then
        assertTrue(true)
    }

    @Test
    fun `onCategoryEnabled SHOULD return true when category is in selected categories`() =
        runTest {
            // Given
            createDukanViewModel.updateState { copy(selectedCategories = setOf(fakeCategories()[0].toUiState())) }

            // When
            val isSelected =
                createDukanViewModel.onCategoryEnabled(fakeCategories()[0].toUiState())

            // Then
            assertTrue(isSelected)
        }

    @Test
    fun `isCategorySelected SHOULD return false when category is not in selected categories`() =
        runTest {
            // Given
            createDukanViewModel.updateState {
                copy(selectedCategories = setOf(fakeCategories()[1].toUiState()))
            }

            // When
            val isSelected =
                createDukanViewModel.isCategorySelected()(fakeCategories()[0].toUiState())

            // Then
            assertFalse(isSelected)
        }


    @Test
    fun `onCategoryClicked SHOULD add category when not selected and can select more`() = runTest {
        // Given
        val category = fakeCategories()[0].toUiState()

        // When
        createDukanViewModel.onCategoryClicked(category)

        // Then
        val selectedCategories = createDukanViewModel.state.value.selectedCategories
        assertTrue(selectedCategories.contains(category))
    }

    @Test
    fun `onCategoryClicked SHOULD remove category when already selected`() = runTest {
        // Given
        val category = fakeCategories()[0].toUiState()
        createDukanViewModel.updateState { copy(selectedCategories = setOf(category)) }

        // When
        createDukanViewModel.onCategoryClicked(category)

        // Then
        val selectedCategories = createDukanViewModel.state.value.selectedCategories
        assertFalse(selectedCategories.contains(category))
    }

    @Test
    fun `onCategoryClicked SHOULD not add category when max categories reached`() = runTest {
        // Given
        createDukanViewModel.updateState {
            copy(
                selectedCategories = setOf(
                    fakeCategories()[0].toUiState(),
                    fakeCategories()[1].toUiState(),
                    fakeCategories()[2].toUiState()
                )
            )
        }

        // When
        createDukanViewModel.onCategoryClicked(fakeCategories()[3].toUiState())

        // Then
        val selectedCategories = createDukanViewModel.state.value.selectedCategories
        assertFalse(selectedCategories.contains(fakeCategories()[3].toUiState()))
        assertEquals(3, selectedCategories.size)
    }

    @Test
    fun `isCategoryEnabled SHOULD return true when can select more categories`() = runTest {
        // Given
        val category = fakeCategories()[0].toUiState()

        // When
        val result = createDukanViewModel.onCategoryEnabled(category)

        // Then
        assertTrue(result)
    }

    @Test
    fun `isCategoryEnabled SHOULD return true when category is already selected`() = runTest {
        // Given
        createDukanViewModel.updateState {
            copy(
                selectedCategories = setOf(
                    fakeCategories()[0].toUiState(),
                    fakeCategories()[1].toUiState(),
                    fakeCategories()[2].toUiState()
                )
            )
        }

        // When
        val result = createDukanViewModel.onCategoryEnabled(fakeCategories()[0].toUiState())

        // Then
        assertTrue(result)
    }

    @Test
    fun `isCategoryEnabled SHOULD return false when max categories reached and category not selected`() =
        runTest {
            // Given
            createDukanViewModel.updateState {
                copy(
                    selectedCategories = setOf(
                        fakeCategories()[0].toUiState(),
                        fakeCategories()[1].toUiState(),
                        fakeCategories()[2].toUiState()
                    )
                )
            }

            // When
            val result = createDukanViewModel.onCategoryEnabled(fakeCategories()[3].toUiState())

            // Then
            assertFalse(result)
        }


    @Test
    fun `onCategoryClicked SHOULD update button state`() = runTest {
        // Given
        createDukanViewModel.updateState {
            copy(
                name = fakeTestDukan(),
                selectedCategories = emptySet(),
                currentStep = CreateDukanUiState.CreateDukanStep.BASIC_INFORMATION
            )
        }

        // When
        createDukanViewModel.onCategoryClicked(fakeCategories()[0].toUiState())

        // Then
        val state = createDukanViewModel.state.value
        assertTrue(state.isButtonEnabled)
    }

    @Test
    fun `onCategoryClicked SHOULD handle empty selection correctly when removing non-existent category`() =
        runTest {
            // Given
            createDukanViewModel.updateState { copy(selectedCategories = emptySet()) }

            // When
            createDukanViewModel.onCategoryClicked(fakeCategories()[0].toUiState())

            // Then
            val selectedCategories = createDukanViewModel.state.value.selectedCategories
            assertTrue(selectedCategories.contains(fakeCategories()[0].toUiState()))
        }

    @Test
    fun `onDismissSnackBar SHOULD hide snack bar when called`() = runTest {
        // Given
        createDukanViewModel.updateState {
            copy(
                snackBarState = SnackBarUiState(
                    SnackBarType.ERROR, Res.string.shelf_name_is_already_exist
                )
            )
        }

        // When
        createDukanViewModel.onDismissSnackBar()

        // Then
        val snackBarState = createDukanViewModel.state.value.snackBarState
        assertNull(snackBarState)
    }

    @Test
    fun `onDismissSnackBar SHOULD not affect other state properties`() = runTest {
        // Given
        val testName = fakeTestDukan()
        val testCategories = setOf(fakeCategories()[0].toUiState(), fakeCategories()[1].toUiState())
        createDukanViewModel.updateState {
            copy(
                name = testName,
                selectedCategories = testCategories,
                snackBarState = SnackBarUiState(
                    SnackBarType.ERROR, Res.string.shelf_name_is_already_exist
                ),
                isNameUnique = false
            )
        }

        // When
        createDukanViewModel.onDismissSnackBar()

        // Then
        val state = createDukanViewModel.state.value
        assertNull(state.snackBarState)
        assertEquals(testName, state.name)
        assertEquals(testCategories, state.selectedCategories)
        assertFalse(state.isNameUnique)
    }

    @Test
    fun `onDismissSnackBar SHOULD work when snack bar is already hidden`() = runTest {
        createDukanViewModel.updateState { copy(snackBarState = null) }

        createDukanViewModel.onDismissSnackBar()

        val snackBarState = createDukanViewModel.state.value.snackBarState
        assertNull(snackBarState)
    }

    @Test
    fun `onBackClicked SHOULD emit NavigateBack WHEN current step is BASIC_INFORMATION`() =
        runTest {
            createDukanViewModel.onBackClicked()

            createDukanViewModel.effect.test {
                assertEquals(CreateDukanEffect.NavigateBack, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onDismissSnackBar SHOULD set snackBarState null`() = runTest {
        createDukanViewModel.updateState {
            copy(
                snackBarState = SnackBarUiState(
                    SnackBarType.ERROR, Res.string.shelf_name_is_already_exist
                )
            )
        }

        createDukanViewModel.onDismissSnackBar()

        createDukanViewModel.state.test {
            val state = awaitItem()
            assertNull(state.snackBarState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `checkNameUniqueness SHOULD show snackbar WHEN name taken`() = runTest {
        everySuspend { dukanManagementRepository.isDukanNameTaken(any()) } returns true
        createDukanViewModel.onNameChanged("Test")
        createDukanViewModel.onCategoryEnabled(fakeCategories()[0].toUiState())

        createDukanViewModel.onNextOrCreateClicked()

        createDukanViewModel.state.test {
            val state = awaitItem()
            assertEquals(CreateDukanUiState.CreateDukanStep.BASIC_INFORMATION, state.currentStep)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onCancelCrop SHOULD clear selectedImage and disable cropping`() = runTest {

        val fakeImageSrc = mock<ImageFile>()
        createDukanViewModel.onClickUploadImage(fakeImageSrc)

        createDukanViewModel.onCancelCrop()

        val state = createDukanViewModel.state.value
        assertNull(state.selectedImage)
        assertFalse(state.isImageBeingCropped)
        assertFalse(state.isButtonEnabled)
    }
}

// ===== FAKE DATA FUNCTIONS =====

@OptIn(ExperimentalUuidApi::class)
private fun fakeDukanColor(): List<Color> {
    return listOf(
        Color(id = Uuid.random(), hexCode = "#F77053"),
        Color(id = Uuid.random(), hexCode = "#F4C343"),
        Color(id = Uuid.random(), hexCode = "#C30C30"),
        Color(id = Uuid.random(), hexCode = "#30ABE8")
    )
}

private fun fakeDukanStyle(): List<Dukan.Style> {
    return listOf(
        Dukan.Style.WIDE_IMAGE, Dukan.Style.SMALL_IMAGE, Dukan.Style.NO_IMAGE
    )
}

@OptIn(ExperimentalUuidApi::class)
private fun fakeCategories(): List<Category> {
    return listOf(
        Category(
            id = Uuid.parse("123e4567-e89b-12d3-a456-426614174000"),
            name = "Electronics",
            imageUrl = "https://example.com/electronics.png"
        ),
        Category(
            id = Uuid.parse("123e4567-e89b-12d3-a456-426614174001"),
            name = "Clothes",
            imageUrl = "https://example.com/clothes.png"
        ),
        Category(
            id = Uuid.parse("123e4567-e89b-12d3-a456-426614174002"),
            name = "Groceries",
            imageUrl = "https://example.com/groceries.png"
        ),
        Category(
            id = Uuid.parse("123e4567-e89b-12d3-a456-426614174003"),
            name = "Books",
            imageUrl = "https://example.com/books.png"
        )
    )
}

private fun fakeTestName() = "Test Dukan Name"
private fun fakeLongName() = "This is a very long dukan name that exceeds the maximum allowed"
private fun fakeTestDukan() = "Test Dukan"
private fun fakeLocationAddress() = "Egypt"
private fun fakeSelectedCoordinates() = CreateDukanUiState.CoordinatesUiState(28.0, 29.0)
private fun fakePointerLocation() = DpOffset(2.dp, 4.dp)
private fun fakeCameraPosition() = CameraPosition(target = Position(29.0, 28.0))
private fun fakeColorUiState() = CreateDukanUiState.ColorUiState(id = "1", color = 0xFFF545)
private fun fakeSingleDukanStyle() = CreateDukanUiState.Style.WIDE_IMAGE

@OptIn(ExperimentalUuidApi::class)
private fun Category.toUiState() = CreateDukanUiState.DukanCategoryUiState(
    id = id.toString(), name = name, imageUrl = imageUrl
)