package net.thechance.mena.dukan.presentation.viewModel.manageDukan

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
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_shelf_successfully
import mena.dukan_presentation.generated.resources.delete_shelf_description
import mena.dukan_presentation.generated.resources.delete_shelf_success
import mena.dukan_presentation.generated.resources.delete_shelf_title
import mena.dukan_presentation.generated.resources.error_for_delete_shelf
import mena.dukan_presentation.generated.resources.shelf_name_is_already_exist
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.exceptions.DukanException
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.util.PagedResult
import net.thechance.mena.dukan.presentation.component.SnackBarType
import net.thechance.mena.dukan.presentation.component.SnackBarUiState
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ManageDukanViewModelTest {

    private val shelfRepository = mock<ShelfRepository>(mode = MockMode.autofill)
    private val productRepository = mock<ProductRepository>(mode = MockMode.autofill)
    private lateinit var manageDukanViewModel: ManageDukanViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        everySuspend { shelfRepository.getMyDukanShelves() } returns dummyShelves
        everySuspend {
            productRepository.getProductsByShelfId(
                any(),
                any(),
                any()
            )
        } returns PagedResult(
            items = emptyList(),
            currentPage = 1,
            totalPages = 1,
            totalItems = 0
        )

        manageDukanViewModel = ManageDukanViewModel(
            shelfRepository,
            productRepository,
            defaultDispatcher = testDispatcher
        )
    }

    @AfterTest
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init SHOULD load shelves with correct count`() = runTest {
        // When
        manageDukanViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals(3, state.shelves.size)
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `init SHOULD select exactly one shelf by default`() = runTest {
        // When
        manageDukanViewModel.state.test {
            val state = awaitItem()
            // Then
            assertNotNull(state.selectedShelf)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init SHOULD select first shelf with correct id`() = runTest {
        // When
        manageDukanViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals("shelf_1", state.selectedShelf?.id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init SHOULD select first shelf with correct name`() = runTest {
        // When
        manageDukanViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals("Electronics", state.selectedShelf?.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onBackButtonClicked SHOULD emit NavigateBack effect`() = runTest {
        // When
        manageDukanViewModel.onBackButtonClicked()

        // Then
        manageDukanViewModel.effect.test {
            assertEquals(ManageDukanEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onAddShelfClicked SHOULD emit NavigateToAddShelf effect`() = runTest {
        // When
        manageDukanViewModel.onAddShelfClicked()

        // Then
        manageDukanViewModel.effect.test {
            assertEquals(ManageDukanEffect.NavigateToAddShelf, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onAddProductClicked SHOULD emit NavigateToAddProduct effect`() = runTest {
        // When
        manageDukanViewModel.onAddProductClicked()

        // Then
        manageDukanViewModel.effect.test {
            assertEquals(ManageDukanEffect.NavigateToAddProduct, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onEditShelfClicked SHOULD emit NavigateToEditShelf effect`() = runTest {
        // When
        manageDukanViewModel.onEditShelfClicked()

        // Then
        manageDukanViewModel.effect.test {
            assertEquals(
                ManageDukanEffect.NavigateToManageShelf(
                    shelfId = "shelf_1",
                    shelfTitle = "Electronics"
                ), awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onProductClick SHOULD emit NavigateToProductDetails effect`() = runTest {
        // Given
        val product = fakeProducts().first().toUiState()

        // When
        manageDukanViewModel.onProductClick(product)

        // Then
        manageDukanViewModel.effect.test {
            assertEquals(ManageDukanEffect.NavigateToProductDetails, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDismissSnackBar SHOULD hide snackbar`() = runTest {
        // Given
        manageDukanViewModel.updateState {
            copy(
                snackBarState = SnackBarUiState(
                    SnackBarType.SUCCESS,
                    Res.string.add_shelf_successfully
                )
            )
        }

        // When
        manageDukanViewModel.onDismissSnackBar()

        // Then
        val state = manageDukanViewModel.state.value
        assertTrue(state.snackBarState == null)
    }

    @Test
    fun `onDismissSnackBar SHOULD reset snackbar state to null`() = runTest {
        // Given
        manageDukanViewModel.updateState {
            copy(
                snackBarState = SnackBarUiState(
                    SnackBarType.ERROR,
                    Res.string.shelf_name_is_already_exist
                )
            )
        }

        // When
        manageDukanViewModel.onDismissSnackBar()

        // Then
        val state = manageDukanViewModel.state.value
        assertTrue(state.snackBarState == null)
    }

    @Test
    fun `isShelfSelected SHOULD return true when shelf is selected`() = runTest {
        // Given
        val shelf = dummyShelvesUiState().first()
        manageDukanViewModel.updateState { copy(selectedShelf = shelf) }

        // When
        val isSelected = manageDukanViewModel.isShelfSelected(shelf)

        // Then
        assertTrue(isSelected)
    }

    @Test
    fun `isShelfSelected SHOULD return false when shelf is not selected`() = runTest {
        // Given
        val shelf = dummyShelvesUiState().first()
        val otherShelf = dummyShelvesUiState()[1]
        manageDukanViewModel.updateState { copy(selectedShelf = shelf) }

        // When
        val isSelected = manageDukanViewModel.isShelfSelected(otherShelf)

        // Then
        assertFalse(isSelected)
    }

    @Test
    fun `onShelfSelected SHOULD select shelf `() = runTest {
        // Given
        val shelf = dummyShelvesUiState()[1]
        manageDukanViewModel.updateState { copy(selectedShelf = dummyShelvesUiState().first()) }

        // When
        manageDukanViewModel.onShelfSelected(shelf)

        // Then
        val selectedShelf = manageDukanViewModel.state.value.selectedShelf
        assertEquals(shelf, selectedShelf)
    }

    @Test
    fun `onShelfSelected SHOULD replace previously selected shelf with new one`() = runTest {
        // Given
        val firstShelf = dummyShelvesUiState().first()
        val secondShelf = dummyShelvesUiState()[1]
        manageDukanViewModel.updateState { copy(selectedShelf = firstShelf) }

        // When
        manageDukanViewModel.onShelfSelected(secondShelf)

        // Then
        val selectedShelf = manageDukanViewModel.state.value.selectedShelf
        assertEquals(secondShelf, selectedShelf)
    }

    @Test
    fun `onShelfSelected SHOULD not contain previous shelf when replacing`() = runTest {
        // Given
        val firstShelf = dummyShelvesUiState().first()
        val secondShelf = dummyShelvesUiState()[1]
        manageDukanViewModel.updateState { copy(selectedShelf = firstShelf) }

        // When
        manageDukanViewModel.onShelfSelected(secondShelf)

        // Then
        val selectedShelf = manageDukanViewModel.state.value.selectedShelf
        assertTrue(selectedShelf != firstShelf)
    }

    @Test
    fun `onShelfSelected SHOULD have exactly one shelf when replacing`() = runTest {
        // Given
        val firstShelf = dummyShelvesUiState().first()
        val secondShelf = dummyShelvesUiState()[1]
        manageDukanViewModel.updateState { copy(selectedShelf = firstShelf) }

        // When
        manageDukanViewModel.onShelfSelected(secondShelf)

        // Then
        val selectedShelf = manageDukanViewModel.state.value.selectedShelf
        assertNotNull(selectedShelf)
    }


    @Test
    fun `onShelfAddedSuccessfully SHOULD show snackbar`() = runTest {
        // When
        manageDukanViewModel.onShelfAddedSuccessfully()

        // Then
        val state = manageDukanViewModel.state.value
        assertTrue(state.snackBarState != null)
    }

    @Test
    fun `onShelfAddedSuccessfully SHOULD show success snackbar type`() = runTest {
        // When
        manageDukanViewModel.onShelfAddedSuccessfully()

        // Then
        val state = manageDukanViewModel.state.value
        assertEquals(SnackBarType.SUCCESS, state.snackBarState?.snackBarType)
    }

    @Test
    fun `onShelfAddedSuccessfully SHOULD show correct success message`() = runTest {
        // When
        manageDukanViewModel.onShelfAddedSuccessfully()

        // Then
        val state = manageDukanViewModel.state.value
        assertEquals(Res.string.add_shelf_successfully, state.snackBarState?.message)
    }


    @Test
    fun `onShelfAddedSuccessfully SHOULD include new shelf in refreshed list`() = runTest {
        // Given
        val newShelves = dummyShelves + Shelf("shelf_4", "New Shelf")
        everySuspend { shelfRepository.getMyDukanShelves() } returns newShelves

        // When
        manageDukanViewModel.onShelfAddedSuccessfully()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = manageDukanViewModel.state.value
        assertTrue(state.shelves.any { it.id == "shelf_4" })
    }


    @Test
    fun `onShelfAddedSuccessfully SHOULD clear products when no shelf selected`() = runTest {
        // Given
        manageDukanViewModel.updateState { copy(selectedShelf = null) }

        // When
        manageDukanViewModel.onShelfAddedSuccessfully()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = manageDukanViewModel.state.value
        assertTrue(state.products.items.isEmpty())
    }

    @Test
    fun `onShelfAddedSuccessfully SHOULD reset product count to zero when no shelf selected`() =
        runTest {
            // Given
            manageDukanViewModel.updateState { copy(selectedShelf = null) }

            // When
            manageDukanViewModel.onShelfAddedSuccessfully()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val state = manageDukanViewModel.state.value
            assertEquals(0, state.totalProducts)
        }


    @Test
    fun `onShelfSelected SHOULD handle empty products gracefully`() = runTest {
        // Given
        val shelf = dummyShelvesUiState().first()
        everySuspend {
            productRepository.getProductsByShelfId(
                shelf.id,
                0,
                10
            )
        } returns PagedResult(
            items = emptyList(),
            currentPage = 1,
            totalPages = 1,
            totalItems = 0
        )

        // When
        manageDukanViewModel.onShelfSelected(shelf)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = manageDukanViewModel.state.value
        assertTrue(state.products.items.isEmpty())
    }

    @Test
    fun `init SHOULD load shelves from mock repository`() = runTest {
        // When
        manageDukanViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals(3, state.shelves.size)
            assertEquals(3, state.shelves.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init SHOULD select first shelf by default from mock data`() = runTest {
        // When
        manageDukanViewModel.state.test {
            val state = awaitItem()
            // Then
            assertNotNull(state.selectedShelf)
            assertEquals("shelf_1", state.selectedShelf.id)
            assertEquals("Electronics", state.selectedShelf.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDismissDeleteShelfConfirmationDialog hides the dialog`() = runTest {
        manageDukanViewModel.state.test {
            val state = awaitItem()
            assertFalse(state.showDeleteConfirmationDialog)
        }
    }

    @Test
    fun `onShowDeleteShelfConfirmationDialog show the dialog`() = runTest {
        manageDukanViewModel.onShowDeleteShelfDailog(
            shelfId = "1"
        )
        manageDukanViewModel.state.test {
            val state = awaitItem()
            assertTrue(state.showDeleteConfirmationDialog)
        }
    }

    @Test
    fun `onShowDeleteShelfConfirmationDialog displays dialog with delete type when no products`() =
        runTest {
            manageDukanViewModel.updateState {
                copy(
                    products = PagingData()
                )
            }
            val deleteShelfConfirmationDialogUiState = DeleteShelfConfirmationDialogUiState(
                title = Res.string.delete_shelf_title,
                description = Res.string.delete_shelf_description,
                type = ConfirmDialogType.DELETE,
                shelfId = "1"
            )
            manageDukanViewModel.onShowDeleteShelfDailog(
                shelfId = "1"
            )

            manageDukanViewModel.state.test {
                val state = awaitItem()
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
            everySuspend { shelfRepository.deleteShelf(shelfId) }

            manageDukanViewModel.onDeleteConfirmed(shelfId)
            advanceUntilIdle()
            manageDukanViewModel.state.test {
                val state = awaitItem()
                assertEquals(snackBarUiState, state.snackBarState)
            }
        }

    @Test
    fun `deleteShelf throw exception should dismiss dialog and show snackBar with error deleting shelf`() =
        runTest {
            val shelfId = "1"
            val snackBarUiState = SnackBarUiState(
                snackBarType = SnackBarType.ERROR,
                message = Res.string.error_for_delete_shelf
            )
            everySuspend { shelfRepository.deleteShelf(shelfId) } throws DukanException("")

            manageDukanViewModel.onDeleteConfirmed(shelfId)
            advanceUntilIdle()
            manageDukanViewModel.state.test {
                val state = awaitItem()
                assertEquals(snackBarUiState, state.snackBarState)
            }
        }
}

// ===== FAKE DATA FUNCTIONS =====

private fun dummyShelvesUiState(): List<ShelfUiState> {
    return listOf(
        ShelfUiState(
            id = "shelf_1",
            name = "Electronics"
        ),
        ShelfUiState(
            id = "shelf_2",
            name = "Clothing"
        ),
        ShelfUiState(
            id = "shelf_3",
            name = "Books"
        )
    )
}

private val dummyShelves = listOf(
    Shelf(
        id = "shelf_1",
        name = "Electronics"
    ),
    Shelf(
        id = "shelf_2",
        name = "Clothing"
    ),
    Shelf(
        id = "shelf_3",
        name = "Books"
    )
)


private fun fakeProducts(): List<Product> {
    return listOf(
        Product(
            id = "product_1",
            name = "iPhone 15",
            description = "Latest iPhone model",
            price = 999.99,
            createdAt = "2023-08-01T10:00:00Z",
            imageUrls = listOf("https://example.com/iphone.jpg")
        ),
        Product(
            id = "product_2",
            name = "MacBook Pro",
            description = "Professional laptop",
            price = 1999.99,
            imageUrls = listOf("https://example.com/macbook.jpg"),
            createdAt = "2023-08-01T10:00:00Z",
        ),
        Product(
            id = "product_3",
            name = "T-Shirt",
            description = "Cotton t-shirt",
            price = 29.99,
            createdAt = "2023-08-01T10:00:00Z",
            imageUrls = listOf("https://example.com/tshirt.jpg")
        )
    )

}
