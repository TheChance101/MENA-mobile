package net.thechance.mena.dukan.presentation.viewModel.approvedDukan

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.delete_shelf_description
import mena.dukan_presentation.generated.resources.delete_shelf_success
import mena.dukan_presentation.generated.resources.delete_shelf_title
import mena.dukan_presentation.generated.resources.dismiss_description
import mena.dukan_presentation.generated.resources.dismiss_title
import mena.dukan_presentation.generated.resources.error_for_delete_shelf
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.presentation.component.SnackBarType
import net.thechance.mena.dukan.presentation.component.SnackBarUiState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ApprovedDukanViewModelTest {

    private var shelfRepository: ShelfRepository = mock(mode = MockMode.autofill)
    private var productRepository: ProductRepository = mock(mode = MockMode.autofill)
    private var viewModel = ApprovedDukanViewModel(shelfRepository, productRepository)

    @Test
    fun `showSnackBar displays correctly with specified message and type`() = runTest {
        val snackBarUiState = SnackBarUiState(
            snackBarType = SnackBarType.ERROR,
            message = Res.string.error_for_delete_shelf
        )
        viewModel.showSnackBar(Res.string.error_for_delete_shelf, SnackBarType.ERROR)
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(snackBarUiState, state.snackBarState)
            assertTrue(state.showSnackBar)
        }

    }

    @Test
    fun `onDismissSnackBar should hide the snackbar`() = runTest {
        val snackBarUiState = SnackBarUiState()
        viewModel.onDismissSnackBar()
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(snackBarUiState, state.snackBarState)
            assertFalse(state.showSnackBar)
        }
    }

    @Test
    fun `onDismissDeleteShelfConfirmationDialog hides the dialog`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.showDeleteConfirmationDialog)
        }
    }


    @Test
    fun `onShowDeleteShelfConfirmationDialog displays dialog with delete type when no products`() =
        runTest {
            val deleteShelfConfirmationDialogUiState = DeleteShelfConfirmationDialogUiState(
                title = Res.string.delete_shelf_title,
                description = Res.string.delete_shelf_description,
                type = ConfirmDialogType.DELETE
            )
            viewModel.onShowDeleteShelfConfirmationDialog()

            viewModel.state.test {
                val state = awaitItem()
                assertTrue(state.showDeleteConfirmationDialog)
                assertEquals(
                    deleteShelfConfirmationDialogUiState,
                    state.deleteShelfConfirmationDialogUiState
                )
            }
        }

    @Test
    fun `onShowDeleteShelfConfirmationDialog displays dialog with dismiss type when shelf has products`() =
        runTest {
            viewModel.updateState {
                copy(
                    products = listOf(
                        Product(
                            id = "1",
                            name = "product 1",
                            description = "description 1",
                            price = 10.5,
                            shelfId = "2",
                            dukanId = "1",
                            imageUrls = emptyList()
                        )
                    )
                )
            }
            val deleteShelfConfirmationDialogUiState = DeleteShelfConfirmationDialogUiState(
                title = Res.string.dismiss_title,
                description = Res.string.dismiss_description,
                type = ConfirmDialogType.DISMISS
            )
            viewModel.onShowDeleteShelfConfirmationDialog()

            viewModel.state.test {
                val state = awaitItem()
                assertTrue(state.showDeleteConfirmationDialog)
                assertEquals(
                    deleteShelfConfirmationDialogUiState,
                    state.deleteShelfConfirmationDialogUiState
                )
            }
        }

    @Test
    fun `deleteShelf successfully should dismiss dialog and show snackBar with delete shelf successfully`() =
        runTest {
            val shelfId = "1"
            val snackBarUiState = SnackBarUiState(
                snackBarType = SnackBarType.SUCCESS,
                message = Res.string.delete_shelf_success
            )
            everySuspend { shelfRepository.deleteShelf(shelfId) } returns true

            viewModel.deleteShelf(shelfId)

            viewModel.state.test {
                val state = awaitItem()
                assertFalse(state.showDeleteConfirmationDialog)
                assertTrue(state.showSnackBar)
                assertEquals(snackBarUiState, state.snackBarState)
            }
        }

    @Test
    fun `deleteShelf return false should dismiss dialog and show snackBar with error deleting shelf`() =
        runTest {
            val shelfId = "1"
            val snackBarUiState = SnackBarUiState(
                snackBarType = SnackBarType.ERROR,
                message = Res.string.error_for_delete_shelf
            )
            everySuspend { shelfRepository.deleteShelf(shelfId) } returns false

            viewModel.deleteShelf(shelfId)

            viewModel.state.test {
                skipItems(1)
                val state = awaitItem()
                assertFalse(state.showDeleteConfirmationDialog)
                assertTrue(state.showSnackBar)
                assertEquals(snackBarUiState, state.snackBarState)
            }
        }
}