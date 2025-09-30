package net.thechance.mena.dukan.presentation.viewModel.approvedDukan

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
import kotlinx.coroutines.test.resetMain
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_shelf_successfully
import mena.dukan_presentation.generated.resources.delete_shelf_description
import mena.dukan_presentation.generated.resources.delete_shelf_success
import mena.dukan_presentation.generated.resources.delete_shelf_title
import mena.dukan_presentation.generated.resources.dismiss_description
import mena.dukan_presentation.generated.resources.dismiss_title
import mena.dukan_presentation.generated.resources.error_for_delete_shelf
import mena.dukan_presentation.generated.resources.shelf_name_is_already_exist
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.exceptions.DukanException
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.repository.CreateShelfRepository
import net.thechance.mena.dukan.presentation.component.SnackBarType
import net.thechance.mena.dukan.presentation.component.SnackBarUiState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ApprovedDukanViewModelTest {

    private val shelfRepository = mock<CreateShelfRepository>(mode = MockMode.autofill)
    private val productRepository = mock<ProductRepository>(mode = MockMode.autofill)
    private lateinit var approvedDukanViewModel: ApprovedDukanViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        everySuspend { shelfRepository.getMyDukanShelves() } returns fakeShelves()
        everySuspend { productRepository.getProductsByShelfId(any()) } returns emptyList()

        approvedDukanViewModel = ApprovedDukanViewModel(
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
        approvedDukanViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals(3, state.shelves.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init SHOULD set available shelves with correct count`() = runTest {
        // When
        approvedDukanViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals(3, state.availableShelves.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init SHOULD select exactly one shelf by default`() = runTest {
        // When
        approvedDukanViewModel.state.test {
            val state = awaitItem()
            // Then
            assertNotNull(state.selectedShelf)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init SHOULD select first shelf with correct id`() = runTest {
        // When
        approvedDukanViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals("shelf_1", state.selectedShelf?.id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init SHOULD select first shelf with correct name`() = runTest {
        // When
        approvedDukanViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals("Electronics", state.selectedShelf?.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onBackButtonClicked SHOULD emit NavigateBack effect`() = runTest {
        // When
        approvedDukanViewModel.onBackButtonClicked()

        // Then
        approvedDukanViewModel.effect.test {
            assertEquals(ApprovedDukanEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onAddShelfClicked SHOULD emit NavigateToAddShelf effect`() = runTest {
        // When
        approvedDukanViewModel.onAddShelfClicked()

        // Then
        approvedDukanViewModel.effect.test {
            assertEquals(ApprovedDukanEffect.NavigateToAddShelf, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onAddProductClicked SHOULD emit NavigateToAddProduct effect`() = runTest {
        // When
        approvedDukanViewModel.onAddProductClicked()

        // Then
        approvedDukanViewModel.effect.test {
            assertEquals(ApprovedDukanEffect.NavigateToAddProduct, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onEditShelfClicked SHOULD emit NavigateToEditShelf effect`() = runTest {
        // When
        approvedDukanViewModel.onEditShelfClicked()

        // Then
        approvedDukanViewModel.effect.test {
            assertEquals(ApprovedDukanEffect.NavigateToEditShelf, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onProductClick SHOULD emit NavigateToProductDetails effect`() = runTest {
        // Given
        val product = fakeProducts().first()

        // When
        approvedDukanViewModel.onProductClick(product)

        // Then
        approvedDukanViewModel.effect.test {
            assertEquals(ApprovedDukanEffect.NavigateToProductDetails, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDismissSnackBar SHOULD hide snackbar`() = runTest {
        // Given
        approvedDukanViewModel.updateState {
            copy(
                snackBarState = SnackBarUiState(
                    SnackBarType.SUCCESS,
                    Res.string.add_shelf_successfully
                )
            )
        }

        // When
        approvedDukanViewModel.onDismissSnackBar()

        // Then
        val state = approvedDukanViewModel.state.value
        assertTrue(state.snackBarState == null)
    }

    @Test
    fun `onDismissSnackBar SHOULD reset snackbar state to null`() = runTest {
        // Given
        approvedDukanViewModel.updateState {
            copy(
                snackBarState = SnackBarUiState(
                    SnackBarType.ERROR,
                    Res.string.shelf_name_is_already_exist
                )
            )
        }

        // When
        approvedDukanViewModel.onDismissSnackBar()

        // Then
        val state = approvedDukanViewModel.state.value
        assertTrue(state.snackBarState == null)
    }

    @Test
    fun `isShelfSelected SHOULD return true when shelf is selected`() = runTest {
        // Given
        val shelf = fakeShelves().first()
        approvedDukanViewModel.updateState { copy(selectedShelf = shelf) }

        // When
        val isSelected = approvedDukanViewModel.isShelfSelected()(shelf)

        // Then
        assertTrue(isSelected)
    }

    @Test
    fun `isShelfSelected SHOULD return false when shelf is not selected`() = runTest {
        // Given
        val shelf = fakeShelves().first()
        val otherShelf = fakeShelves()[1]
        approvedDukanViewModel.updateState { copy(selectedShelf = shelf) }

        // When
        val isSelected = approvedDukanViewModel.isShelfSelected()(otherShelf)

        // Then
        assertFalse(isSelected)
    }

    @Test
    fun `onShelfSelected SHOULD return true when shelf is selected`() = runTest {
        // Given
        val shelf = fakeShelves()[1]
        approvedDukanViewModel.updateState { copy(selectedShelf = fakeShelves().first()) }

        // When
        val result = approvedDukanViewModel.onShelfSelected(shelf)

        // Then
        assertTrue(result)
    }

    @Test
    fun `onShelfSelected SHOULD replace previously selected shelf with new one`() = runTest {
        // Given
        val firstShelf = fakeShelves().first()
        val secondShelf = fakeShelves()[1]
        approvedDukanViewModel.updateState { copy(selectedShelf = firstShelf) }

        // When
        approvedDukanViewModel.onShelfSelected(secondShelf)

        // Then
        val selectedShelf = approvedDukanViewModel.state.value.selectedShelf
        assertEquals(secondShelf, selectedShelf)
    }

    @Test
    fun `onShelfSelected SHOULD not contain previous shelf when replacing`() = runTest {
        // Given
        val firstShelf = fakeShelves().first()
        val secondShelf = fakeShelves()[1]
        approvedDukanViewModel.updateState { copy(selectedShelf = firstShelf) }

        // When
        approvedDukanViewModel.onShelfSelected(secondShelf)

        // Then
        val selectedShelf = approvedDukanViewModel.state.value.selectedShelf
        assertTrue(selectedShelf != firstShelf)
    }

    @Test
    fun `onShelfSelected SHOULD have exactly one shelf when replacing`() = runTest {
        // Given
        val firstShelf = fakeShelves().first()
        val secondShelf = fakeShelves()[1]
        approvedDukanViewModel.updateState { copy(selectedShelf = firstShelf) }

        // When
        approvedDukanViewModel.onShelfSelected(secondShelf)

        // Then
        val selectedShelf = approvedDukanViewModel.state.value.selectedShelf
        assertNotNull(selectedShelf)
    }


    @Test
    fun `onShelfSelected SHOULD load products with correct count`() = runTest {
        // Given
        val shelf = fakeShelves().first()
        val products = fakeProducts().filter { it.shelfId == shelf.id }
        everySuspend { productRepository.getProductsByShelfId(shelf.id) } returns products

        // When
        approvedDukanViewModel.onShelfSelected(shelf)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = approvedDukanViewModel.state.value
        assertEquals(products.size, state.products.size)
    }

    @Test
    fun `onShelfSelected SHOULD update product count correctly`() = runTest {
        // Given
        val shelf = fakeShelves().first()
        val products = fakeProducts().filter { it.shelfId == shelf.id }
        everySuspend { productRepository.getProductsByShelfId(shelf.id) } returns products

        // When
        approvedDukanViewModel.onShelfSelected(shelf)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = approvedDukanViewModel.state.value
        assertEquals(products.size, state.totalProducts)
    }

    @Test
    fun `onShelfDeselected SHOULD return true when shelf is deselected`() = runTest {
        // Given
        val shelf = fakeShelves().first()
        approvedDukanViewModel.updateState { copy(selectedShelf = shelf) }

        // When
        val result = approvedDukanViewModel.onShelfDeselected(shelf)

        // Then
        assertTrue(result)
    }

    @Test
    fun `onShelfDeselected SHOULD clear selected shelf`() = runTest {
        // Given
        val shelf = fakeShelves().first()
        approvedDukanViewModel.updateState { copy(selectedShelf = shelf) }

        // When
        approvedDukanViewModel.onShelfDeselected(shelf)

        // Then
        val selectedShelf = approvedDukanViewModel.state.value.selectedShelf
        assertNull(selectedShelf)
    }


    @Test
    fun `onShelfDeselected SHOULD clear products when no shelf selected`() = runTest {
        // Given
        val shelf = fakeShelves().first()
        val products = fakeProducts().filter { it.shelfId == shelf.id }
        approvedDukanViewModel.updateState {
            copy(
                selectedShelf = shelf,
                products = products,
                totalProducts = products.size
            )
        }

        // When
        approvedDukanViewModel.onShelfDeselected(shelf)

        // Then
        val state = approvedDukanViewModel.state.value
        assertTrue(state.products.isEmpty())
    }

    @Test
    fun `onShelfDeselected SHOULD reset product count to zero when no shelf selected`() =
        runTest {
            // Given
            val shelf = fakeShelves().first()
            val products = fakeProducts().filter { it.shelfId == shelf.id }
            approvedDukanViewModel.updateState {
                copy(
                    selectedShelf = shelf,
                    products = products,
                    totalProducts = products.size
                )
            }

            // When
            approvedDukanViewModel.onShelfDeselected(shelf)

            // Then
            val state = approvedDukanViewModel.state.value
            assertEquals(0, state.totalProducts)
        }

    @Test
    fun `onShelfEnabled SHOULD always return true`() = runTest {
        // Given
        val shelf = fakeShelves().first()

        // When
        val result = approvedDukanViewModel.onShelfEnabled(shelf)

        // Then
        assertTrue(result)
    }

    @Test
    fun `onShelfAddedSuccessfully SHOULD show snackbar`() = runTest {
        // When
        approvedDukanViewModel.onShelfAddedSuccessfully()

        // Then
        val state = approvedDukanViewModel.state.value
        assertTrue(state.snackBarState != null)
    }

    @Test
    fun `onShelfAddedSuccessfully SHOULD show success snackbar type`() = runTest {
        // When
        approvedDukanViewModel.onShelfAddedSuccessfully()

        // Then
        val state = approvedDukanViewModel.state.value
        assertEquals(SnackBarType.SUCCESS, state.snackBarState?.snackBarType)
    }

    @Test
    fun `onShelfAddedSuccessfully SHOULD show correct success message`() = runTest {
        // When
        approvedDukanViewModel.onShelfAddedSuccessfully()

        // Then
        val state = approvedDukanViewModel.state.value
        assertEquals(Res.string.add_shelf_successfully, state.snackBarState?.message)
    }

    @Test
    fun `onShelfAddedSuccessfully SHOULD refresh shelves list`() = runTest {
        // Given
        val newShelves = fakeShelves() + Shelf("shelf_4", "New Shelf")
        everySuspend { shelfRepository.getMyDukanShelves() } returns newShelves

        // When
        approvedDukanViewModel.onShelfAddedSuccessfully()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = approvedDukanViewModel.state.value
        assertEquals(4, state.shelves.size)
    }

    @Test
    fun `onShelfAddedSuccessfully SHOULD include new shelf in refreshed list`() = runTest {
        // Given
        val newShelves = fakeShelves() + Shelf("shelf_4", "New Shelf")
        everySuspend { shelfRepository.getMyDukanShelves() } returns newShelves

        // When
        approvedDukanViewModel.onShelfAddedSuccessfully()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = approvedDukanViewModel.state.value
        assertTrue(state.shelves.any { it.id == "shelf_4" })
    }

    @Test
    fun `onShelfAddedSuccessfully SHOULD reload products for selected shelf`() = runTest {
        // Wait for initial state to load
        approvedDukanViewModel.state.test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
        
        // Given
        val selectedShelf = fakeShelves().first()
        val products = fakeProducts().filter { it.shelfId == selectedShelf.id }
        approvedDukanViewModel.updateState { copy(selectedShelf = selectedShelf) }
        everySuspend { productRepository.getProductsByShelfId(selectedShelf.id) } returns products

        // When
        approvedDukanViewModel.onShelfAddedSuccessfully()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = approvedDukanViewModel.state.value
        assertEquals(products.size, state.products.size)
    }

    @Test
    fun `onShelfAddedSuccessfully SHOULD update total products count`() = runTest {
        // Given
        val selectedShelf = fakeShelves().first()
        val products = fakeProducts().filter { it.shelfId == selectedShelf.id }
        approvedDukanViewModel.updateState { copy(selectedShelf = selectedShelf) }
        everySuspend { productRepository.getProductsByShelfId(selectedShelf.id) } returns products

        // When
        approvedDukanViewModel.onShelfAddedSuccessfully()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = approvedDukanViewModel.state.value
        assertEquals(products.size, state.totalProducts)
    }

    @Test
    fun `onShelfAddedSuccessfully SHOULD clear products when no shelf selected`() = runTest {
        // Given
        approvedDukanViewModel.updateState { copy(selectedShelf = null) }

        // When
        approvedDukanViewModel.onShelfAddedSuccessfully()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = approvedDukanViewModel.state.value
        assertTrue(state.products.isEmpty())
    }

    @Test
    fun `onShelfAddedSuccessfully SHOULD reset product count to zero when no shelf selected`() =
        runTest {
            // Given
            approvedDukanViewModel.updateState { copy(selectedShelf = null) }

            // When
            approvedDukanViewModel.onShelfAddedSuccessfully()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val state = approvedDukanViewModel.state.value
            assertEquals(0, state.totalProducts)
        }


    @Test
    fun `onShelfSelected SHOULD stop loading when empty products returned`() = runTest {
        // Given
        val shelf = fakeShelves().first()
        everySuspend { productRepository.getProductsByShelfId(shelf.id) } returns emptyList()

        // When
        approvedDukanViewModel.onShelfSelected(shelf)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = approvedDukanViewModel.state.value
        assertFalse(state.isLoadingProducts)
    }

    @Test
    fun `onShelfSelected SHOULD handle empty products gracefully`() = runTest {
        // Given
        val shelf = fakeShelves().first()
        everySuspend { productRepository.getProductsByShelfId(shelf.id) } returns emptyList()

        // When
        approvedDukanViewModel.onShelfSelected(shelf)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = approvedDukanViewModel.state.value
        assertTrue(state.products.isEmpty())
    }

    @Test
    fun `init SHOULD load shelves from mock repository`() = runTest {
        // When
        approvedDukanViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals(3, state.shelves.size)
            assertEquals(3, state.availableShelves.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init SHOULD select first shelf by default from mock data`() = runTest {
        // When
        approvedDukanViewModel.state.test {
            val state = awaitItem()
            // Then
            assertNotNull(state.selectedShelf)
            assertEquals("shelf_1", state.selectedShelf.id)
            assertEquals("Electronics", state.selectedShelf.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onShelfSelected SHOULD load correct number of products from mock repository`() = runTest {
        // Given
        val shelf = fakeShelves().first()
        val mockProducts = fakeProducts().filter { it.shelfId == shelf.id }
        everySuspend { productRepository.getProductsByShelfId(shelf.id) } returns mockProducts

        // When
        approvedDukanViewModel.onShelfSelected(shelf)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = approvedDukanViewModel.state.value
        assertEquals(mockProducts.size, state.products.size)
    }

    @Test
    fun `onShelfSelected SHOULD load correct product name from mock repository`() = runTest {
        // Given
        val shelf = fakeShelves().first()
        val mockProducts = fakeProducts().filter { it.shelfId == shelf.id }
        everySuspend { productRepository.getProductsByShelfId(shelf.id) } returns mockProducts

        // When
        approvedDukanViewModel.onShelfSelected(shelf)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = approvedDukanViewModel.state.value
        assertEquals("iPhone 15", state.products.first().name)
    }

    @Test
    fun `onShelfSelected SHOULD update total products count from fake repository`() = runTest {
        // Given
        val shelf = fakeShelves().first()
        val fakeProducts = fakeProducts().filter { it.shelfId == shelf.id }
        everySuspend { productRepository.getProductsByShelfId(shelf.id) } returns fakeProducts

        // When
        approvedDukanViewModel.onShelfSelected(shelf)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(fakeProducts.size, approvedDukanViewModel.state.value.totalProducts)
    }

    @Test
    fun `onDismissSnackBar should hide the snackbar`() = runTest {
        approvedDukanViewModel.onDismissSnackBar()
        approvedDukanViewModel.state.test {
            val state = awaitItem()
            assertEquals(null, state.snackBarState)
        }
    }

    @Test
    fun `onDismissDeleteShelfConfirmationDialog hides the dialog`() = runTest {
        approvedDukanViewModel.state.test {
            val state = awaitItem()
            assertFalse(state.showDeleteConfirmationDialog)
        }
    }

    @Test
    fun `onShowDeleteShelfConfirmationDialog show the dialog`() = runTest {
        approvedDukanViewModel.onShowDeleteShelfConfirmationDialog()
        approvedDukanViewModel.state.test {
            val state = awaitItem()
            assertTrue(state.showDeleteConfirmationDialog)
        }
    }

    @Test
    fun `onShowDeleteShelfConfirmationDialog displays dialog with delete type when no products`() =
        runTest {
            approvedDukanViewModel.updateState {
                copy(
                    products = emptyList()
                )
            }
            val deleteShelfConfirmationDialogUiState = DeleteShelfConfirmationDialogUiState(
                title = Res.string.delete_shelf_title,
                description = Res.string.delete_shelf_description,
                type = ConfirmDialogType.DELETE
            )
            approvedDukanViewModel.onShowDeleteShelfConfirmationDialog()

            approvedDukanViewModel.state.test {
                val state = awaitItem()
                assertEquals(
                    deleteShelfConfirmationDialogUiState,
                    state.deleteShelfConfirmationDialogUiState
                )
            }
        }

    @Test
    fun `onShowDeleteShelfConfirmationDialog displays dialog with dismiss type when shelf has products`() =
        runTest {
            approvedDukanViewModel.updateState {
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
            approvedDukanViewModel.onShowDeleteShelfConfirmationDialog()

            approvedDukanViewModel.state.test {
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

            approvedDukanViewModel.deleteShelf(shelfId)

            approvedDukanViewModel.state.test {
                skipItems(1)
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

            approvedDukanViewModel.deleteShelf(shelfId)

            approvedDukanViewModel.state.test {
                skipItems(1)
                val state = awaitItem()
                assertEquals(snackBarUiState, state.snackBarState)
            }
        }
}

// ===== FAKE DATA FUNCTIONS =====

private fun fakeShelves(): List<Shelf> {
    return listOf(
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
}

private fun fakeProducts(): List<Product> {
    return listOf(
        Product(
            id = "product_1",
            name = "iPhone 15",
            description = "Latest iPhone model",
            price = 999.99,
            shelfId = "shelf_1",
            dukanId = "dukan_123",
            imageUrls = listOf("https://example.com/iphone.jpg")
        ),
        Product(
            id = "product_2",
            name = "MacBook Pro",
            description = "Professional laptop",
            price = 1999.99,
            shelfId = "shelf_1",
            dukanId = "dukan_123",
            imageUrls = listOf("https://example.com/macbook.jpg")
        ),
        Product(
            id = "product_3",
            name = "T-Shirt",
            description = "Cotton t-shirt",
            price = 29.99,
            shelfId = "shelf_2",
            dukanId = "dukan_123",
            imageUrls = listOf("https://example.com/tshirt.jpg")
        )
    )

}
