package net.thechance.mena.dukan.presentation.viewModel.createShelf

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mena.dukan_presentation.generated.resources.Res
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.repository.CreateShelfRepository
import net.thechance.mena.dukan.presentation.component.SnackBarUiState
import net.thechance.mena.dukan.presentation.component.SnackBarType
import mena.dukan_presentation.generated.resources.shelf_name_is_already_exist
import mena.dukan_presentation.generated.resources.shelf_name_is_invalid
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CreateShelfViewModelTest {

    private val shelfRepository = mock<CreateShelfRepository>(mode = MockMode.autofill)
    private lateinit var createShelfViewModel: CreateShelfViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        everySuspend { shelfRepository.getMyDukanShelves() } returns emptyList()
        createShelfViewModel = CreateShelfViewModel(shelfRepository, testDispatcher)
    }

    @Test
    fun `onTitleChanged SHOULD update shelfTitle and enable button for valid input`() = runTest {
        val title = "Valid Shelf"

        createShelfViewModel.state.test {
            createShelfViewModel.onTitleChanged(title)

            skipItems(1)
            val state = awaitItem()

            assertEquals(title, state.shelfTitle)
            assertTrue(state.isCreateButtonEnabled)
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `onTitleChanged SHOULD trim and disable button for invalid input`() = runTest {
        val title = " 123 "

        createShelfViewModel.state.test {
            createShelfViewModel.onTitleChanged(title)
            skipItems(1)
            val state = awaitItem()
            assertEquals("123", state.shelfTitle)
            assertFalse(state.isCreateButtonEnabled)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onCreateButtonClicked SHOULD show snack bar when title is blank`() = runTest {
        createShelfViewModel.onCreateButtonClicked()

        createShelfViewModel.state.test {
            val state = awaitItem()

            assertTrue(state.snackBarState != null)
            assertEquals(SnackBarType.ERROR, state.snackBarState.snackBarType)
            assertEquals(Res.string.shelf_name_is_invalid, state.snackBarState.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onCreateButtonClicked SHOULD show snack bar when shelf name exists`() = runTest {
        val existingShelf = Shelf(id = "1", name = "Existing")
        everySuspend { shelfRepository.getMyDukanShelves() } returns listOf(existingShelf)

        createShelfViewModel.onTitleChanged("Existing")
        createShelfViewModel.onCreateButtonClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = createShelfViewModel.state.value
        assertTrue(state.snackBarState != null)
        assertEquals(SnackBarType.ERROR, state.snackBarState.snackBarType)
        assertEquals(Res.string.shelf_name_is_already_exist, state.snackBarState.message)
    }

    @Test
    fun `onCreateButtonClicked SHOULD show snack bar on repository error`() = runTest {
        everySuspend { shelfRepository.getMyDukanShelves() } throws RuntimeException("fail")

        createShelfViewModel.onTitleChanged("ErrorShelf")
        createShelfViewModel.onCreateButtonClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = createShelfViewModel.state.value
        assertTrue(state.snackBarState != null)
        assertEquals(SnackBarType.ERROR, state.snackBarState.snackBarType)
        assertEquals(Res.string.shelf_name_is_already_exist, state.snackBarState.message)
    }

    @Test
    fun `onDismissSnackBar SHOULD hide snack bar`() = runTest {
        createShelfViewModel.updateState {
            copy(snackBarState = SnackBarUiState(SnackBarType.ERROR, Res.string.shelf_name_is_already_exist))
        }

        createShelfViewModel.onDismissSnackBar()

        val state = createShelfViewModel.state.value
        assertTrue(state.snackBarState == null)
    }
}