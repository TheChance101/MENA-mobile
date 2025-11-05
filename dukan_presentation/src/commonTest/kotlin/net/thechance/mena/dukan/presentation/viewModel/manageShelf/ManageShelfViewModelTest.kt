package net.thechance.mena.dukan.presentation.viewModel.manageShelf

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_shelf_successfully
import mena.dukan_presentation.generated.resources.error_edit_shelf
import mena.dukan_presentation.generated.resources.no_internet_message
import mena.dukan_presentation.generated.resources.shelf_name_is_already_exist
import mena.dukan_presentation.generated.resources.shelf_name_is_invalid
import mena.dukan_presentation.generated.resources.shelf_name_is_not_changed
import net.thechance.mena.dukan.domain.exceptions.DuplicateNameException
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ManageShelfViewModelTest {
    private lateinit var manageShelfViewModel: ManageShelfViewModel
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var shelfRepository: ShelfRepository
    private val testDispatcher = StandardTestDispatcher()
    private val expectedShelfId = "1"
    private val expectedShelfTitle = "Clothing"

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        savedStateHandle = SavedStateHandle(
            mapOf(
                "shelfId" to expectedShelfId,
                "shelfTitle" to expectedShelfTitle
            )
        )
        shelfRepository = mock<ShelfRepository>(mode = MockMode.autofill)
        everySuspend {
            shelfRepository.updateShelf(
                any(),
                any()
            )
        } returns Unit
        manageShelfViewModel = ManageShelfViewModel(
            shelfRepository = shelfRepository,
            savedStateHandle = savedStateHandle,
            defaultDispatcher = testDispatcher
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should initialize state with shelf id and title from saved state handle`() {
        manageShelfViewModel.updateState {
            copy(
                shelfTitle = expectedShelfTitle
            )
        }
        val state = manageShelfViewModel.state.value
        assertEquals(expectedShelfTitle, state.shelfTitle)
    }


    @Test
    fun `init should set shelf title to empty when shelf title is null`() = runTest {
        manageShelfViewModel.updateState {
            copy(
                shelfTitle = ""
            )
        }

        manageShelfViewModel.state.test {
            val state = awaitItem()
            assertEquals("", state.shelfTitle)
        }
    }

    @Test
    fun `onBackClicked should emit NavigateBack effect`() = runTest {
        manageShelfViewModel.onBackClicked()

        val actualEffect = manageShelfViewModel.effect.first()

        assertEquals(ManageShelfEffect.NavigateBack, actualEffect)
    }

    @Test
    fun `onTitleChanged should update shelfTitle in state`() = runTest {
        val newTitle = "New Shelf Title"
        manageShelfViewModel.onShelfNameChange(newTitle)
        val actualState = manageShelfViewModel.state.first()
        assertEquals(newTitle, actualState.shelfTitle)
    }

    @Test
    fun `onShelfNameChange SHOULD trim and disable button for invalid input`() = runTest {
        val blankTitle = " "

        manageShelfViewModel.state.test {
            manageShelfViewModel.onShelfNameChange(blankTitle)
            skipItems(1)
            val state = awaitItem()
            assertEquals(" ", state.shelfTitle)
            assertFalse(state.isSaveButtonEnabled)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSaveClicked SHOULD show error snackbar when title is blank`() = runTest {

        manageShelfViewModel.onShelfNameChange("   ")

        manageShelfViewModel.onSaveClicked()

        manageShelfViewModel.state.test {
            val updatedState = awaitItem()
            assertTrue(updatedState.snackBarState != null)
            assertEquals(SnackBarType.ERROR, updatedState.snackBarState.snackBarType)
            assertEquals(Res.string.shelf_name_is_invalid, updatedState.snackBarState.message)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDismissSnackBar SHOULD hide snack bar`() = runTest {
        manageShelfViewModel.updateState {
            copy(
                snackBarState = SnackBarUiState(
                    SnackBarType.SUCCESS,
                    Res.string.add_shelf_successfully
                )
            )
        }

        manageShelfViewModel.onDismissSnackBar()

        val state = manageShelfViewModel.state.value
        assertEquals(state.snackBarState, null)
    }

    @Test
    fun `onSaveClicked SHOULD show error snackbar when shelf name is same as current`() = runTest {
        manageShelfViewModel.onShelfNameChange(expectedShelfTitle)

        everySuspend {
            shelfRepository.updateShelf(
                any(),
                any()
            )
        } throws DuplicateNameException("Shelf name not changed")

        manageShelfViewModel.onSaveClicked()
        testScheduler.advanceUntilIdle()

        val snackBarState = manageShelfViewModel.state.value.snackBarState
        assertNotNull(snackBarState)
        assertEquals(Res.string.shelf_name_is_not_changed, snackBarState.message)
        assertEquals(SnackBarType.ERROR, snackBarState.snackBarType)
    }

    @Test
    fun `onSaveClicked SHOULD edit shelf and emit NavigateBackWithEditedShelfName on success`() =
        runTest {
            val shelfId = "123"
            val newName = "Updated Shelf"

            manageShelfViewModel.onShelfNameChange(newName)

            everySuspend { shelfRepository.updateShelf(shelfId, newName) } returns Unit

            manageShelfViewModel.effect.test {
                manageShelfViewModel.onSaveClicked()
                testDispatcher.scheduler.advanceUntilIdle()

                assertEquals(ManageShelfEffect.NavigateBackWithEditedShelfName, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onEditShelfError SHOULD show no internet snackbar when NoInternetException occurs`() = runTest {
        val exception = NoInternetException()

        everySuspend { shelfRepository.updateShelf(any(),any()) } throws exception

        manageShelfViewModel.onSaveClicked()

        advanceUntilIdle()
        manageShelfViewModel.state.test {
            val state = awaitItem()
            assertNotNull(state.snackBarState)
            assertEquals(Res.string.no_internet_message, state.snackBarState.message)
            assertEquals(SnackBarType.ERROR, state.snackBarState.snackBarType)
        }
    }

    @Test
    fun `onEditShelfError SHOULD show duplicate name snackbar when DuplicateNameException occurs`() = runTest {
        val exception = DuplicateNameException("duplicate")

        everySuspend { shelfRepository.updateShelf(any(), any()) } throws exception

        manageShelfViewModel.onSaveClicked()

        advanceUntilIdle()
        manageShelfViewModel.state.test {
            val state = awaitItem()
            assertNotNull(state.snackBarState)
            assertEquals(Res.string.shelf_name_is_not_changed, state.snackBarState.message)
            assertEquals(SnackBarType.ERROR, state.snackBarState.snackBarType)
        }
    }

    @Test
    fun `onEditShelfError SHOULD show general error snackbar for other exceptions`() = runTest {
        val exception = Exception("Unknown error")

        everySuspend { shelfRepository.updateShelf(any(), any()) } throws exception

        manageShelfViewModel.onSaveClicked()

        advanceUntilIdle()
        manageShelfViewModel.state.test {
            val state = awaitItem()
            assertNotNull(state.snackBarState)
            assertEquals(Res.string.error_edit_shelf, state.snackBarState.message)
            assertEquals(SnackBarType.ERROR, state.snackBarState.snackBarType)
        }
    }

}