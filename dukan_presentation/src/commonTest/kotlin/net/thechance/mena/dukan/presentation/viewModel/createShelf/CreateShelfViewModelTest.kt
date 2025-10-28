package net.thechance.mena.dukan.presentation.viewModel.createShelf

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.shelf_name_is_already_exist
import mena.dukan_presentation.generated.resources.shelf_name_is_invalid
import mena.dukan_presentation.generated.resources.something_went_wrong
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.exceptions.DuplicateNameException
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalCoroutinesApi::class)
class CreateShelfViewModelTest {

    private val shelfRepository = mock<ShelfRepository>(mode = MockMode.autofill)
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
        val blankTitle = " "

        createShelfViewModel.state.test {
            createShelfViewModel.onTitleChanged(blankTitle)
            skipItems(1)
            val state = awaitItem()
            assertEquals(" ", state.shelfTitle)
            assertFalse(state.isCreateButtonEnabled)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onCreateButtonClicked SHOULD show snack bar when title is blank`() = runTest {
        createShelfViewModel.onCreateClicked()

        createShelfViewModel.state.test {
            val state = awaitItem()

            assertTrue(state.snackBarState != null)
            assertEquals(SnackBarType.ERROR, state.snackBarState.snackBarType)
            assertEquals(Res.string.shelf_name_is_invalid, state.snackBarState.message)
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `onDismissSnackBar SHOULD hide snack bar`() = runTest {
        createShelfViewModel.updateState {
            copy(
                snackBarState = SnackBarUiState(
                    SnackBarType.ERROR,
                    Res.string.shelf_name_is_already_exist
                )
            )
        }

        createShelfViewModel.onDismissSnackBar()

        val state = createShelfViewModel.state.value
        assertTrue(state.snackBarState == null)
    }

    @Test
    fun `onBackButtonClicked SHOULD emit NavigateBack effect`() = runTest {
        createShelfViewModel.effect.test {
            createShelfViewModel.onBackClicked()
            assertEquals(CreateShelfEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `onCreateButtonClicked SHOULD create shelf and navigate to manage dukan on success`() =
        runTest {
            val title = "New Shelf"
            createShelfViewModel.onTitleChanged(title)

            everySuspend {
                shelfRepository.createShelf(
                    Shelf(
                        id = Uuid.random(),
                        name = "fake"
                    )
                )
            } returns Unit

            createShelfViewModel.effect.test {
                createShelfViewModel.onCreateClicked()
                testDispatcher.scheduler.advanceUntilIdle()

                // Check state isLoading reset
                val state = createShelfViewModel.state.value
                assertFalse(state.isLoading)

                // Check navigation effect emitted
                assertEquals(CreateShelfEffect.NavigateToManageDukan, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }


    @Test
    fun `onCreateButtonClicked SHOULD show general error when unknown exception thrown`() =
        runTest {
            val title = "Failing Shelf"
            createShelfViewModel.onTitleChanged(title)

            everySuspend { shelfRepository.createShelf(any()) } throws RuntimeException("Unexpected")

            createShelfViewModel.onCreateClicked()
            testDispatcher.scheduler.advanceUntilIdle()

            val state = createShelfViewModel.state.value
            assertFalse(state.isLoading)
            assertTrue(state.snackBarState != null)
            assertEquals(SnackBarType.ERROR, state.snackBarState.snackBarType)
            assertEquals(Res.string.something_went_wrong, state.snackBarState.message)
        }

    @Test
    fun `onCreateButtonClicked SHOULD show already exist error when ShelfNameTakenException thrown`() =
        runTest {
            val title = "Existing Shelf"
            createShelfViewModel.onTitleChanged(title)

            everySuspend { shelfRepository.createShelf(any()) } throws DuplicateNameException()

            createShelfViewModel.onCreateClicked()
            testDispatcher.scheduler.advanceUntilIdle()

            val state = createShelfViewModel.state.value
            assertFalse(state.isLoading)
            assertTrue(state.snackBarState != null)
            assertEquals(SnackBarType.ERROR, state.snackBarState.snackBarType)
            assertEquals(Res.string.shelf_name_is_already_exist, state.snackBarState.message)
        }

}