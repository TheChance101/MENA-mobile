package net.thechance.mena.dukan.presentation.viewModel.approvedDukan

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ApprovedDukanViewModelTest {

    private val shelfRepository = mock<ShelfRepository>(mode = MockMode.autofill)
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
            testDispatcher
        )
    }

    @Test
    fun `init SHOULD load shelves with correct count`() = runTest {
        // When
        approvedDukanViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals(fakeShelves().size, state.shelves.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init SHOULD set available shelves with correct count`() = runTest {
        // When
        approvedDukanViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals(fakeShelves().size, state.availableShelves.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init SHOULD select exactly one shelf by default`() = runTest {
        // When
        approvedDukanViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals(1, state.selectedShelves.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init SHOULD select first shelf from available shelves`() = runTest {
        // When
        approvedDukanViewModel.state.test {
            val state = awaitItem()
            // Then
            assertEquals(fakeShelves().first(), state.selectedShelves.first())
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
                showSnackBar = true,
                showShelfAddedSuccess = true
            )
        }

        // When
        approvedDukanViewModel.onDismissSnackBar()

        // Then
        val state = approvedDukanViewModel.state.value
        assertFalse(state.showSnackBar)
    }

    @Test
    fun `onDismissSnackBar SHOULD reset success flag`() = runTest {
        // Given
        approvedDukanViewModel.updateState {
            copy(
                showSnackBar = true,
                showShelfAddedSuccess = true
            )
        }

        // When
        approvedDukanViewModel.onDismissSnackBar()

        // Then
        val state = approvedDukanViewModel.state.value
        assertFalse(state.showShelfAddedSuccess)
    }

    @Test
    fun `isShelfSelected SHOULD return true when shelf is in selected shelves`() = runTest {
        // Given
        val shelf = fakeShelves().first()
        approvedDukanViewModel.updateState { copy(selectedShelves = setOf(shelf)) }

        // When
        val isSelected = approvedDukanViewModel.isShelfSelected()(shelf)

        // Then
        assertTrue(isSelected)
    }

    @Test
    fun `isShelfSelected SHOULD return false when shelf is not in selected shelves`() = runTest {
        // Given
        val shelf = fakeShelves().first()
        val otherShelf = fakeShelves()[1]
        approvedDukanViewModel.updateState { copy(selectedShelves = setOf(shelf)) }

        // When
        val isSelected = approvedDukanViewModel.isShelfSelected()(otherShelf)

        // Then
        assertFalse(isSelected)
    }

    @Test
    fun `onShelfSelected SHOULD return true when shelf is added`() = runTest {
        // Given
        val shelf = fakeShelves()[1]
        approvedDukanViewModel.updateState { copy(selectedShelves = setOf(fakeShelves().first())) }

        // When
        val result = approvedDukanViewModel.onShelfSelected(shelf)

        // Then
        assertTrue(result)
    }

    @Test
    fun `onShelfSelected SHOULD add shelf to selected shelves`() = runTest {
        // Given
        val shelf = fakeShelves()[1]
        approvedDukanViewModel.updateState { copy(selectedShelves = setOf(fakeShelves().first())) }

        // When
        approvedDukanViewModel.onShelfSelected(shelf)

        // Then
        val selectedShelves = approvedDukanViewModel.state.value.selectedShelves
        assertTrue(selectedShelves.contains(shelf))
    }

    @Test
    fun `onShelfSelected SHOULD load products with correct count`() = runTest {
        // Given
        val shelf = fakeShelves().first()
        val products = fakeProducts()
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
        val products = fakeProducts()
        everySuspend { productRepository.getProductsByShelfId(shelf.id) } returns products

        // When
        approvedDukanViewModel.onShelfSelected(shelf)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = approvedDukanViewModel.state.value
        assertEquals(products.size, state.totalProducts)
    }

    @Test
    fun `onShelfDeselected SHOULD return true when shelf is removed`() = runTest {
        // Given
        val shelf = fakeShelves().first()
        approvedDukanViewModel.updateState { copy(selectedShelves = setOf(shelf)) }

        // When
        val result = approvedDukanViewModel.onShelfDeselected(shelf)

        // Then
        assertTrue(result)
    }

    @Test
    fun `onShelfDeselected SHOULD remove shelf from selected shelves`() = runTest {
        // Given
        val shelf = fakeShelves().first()
        approvedDukanViewModel.updateState { copy(selectedShelves = setOf(shelf)) }

        // When
        approvedDukanViewModel.onShelfDeselected(shelf)

        // Then
        val selectedShelves = approvedDukanViewModel.state.value.selectedShelves
        assertFalse(selectedShelves.contains(shelf))
    }

    @Test
    fun `onShelfDeselected SHOULD clear products when no shelves selected`() = runTest {
        // Given
        val shelf = fakeShelves().first()
        approvedDukanViewModel.updateState {
            copy(
                selectedShelves = setOf(shelf),
                products = fakeProducts(),
                totalProducts = fakeProducts().size
            )
        }

        // When
        approvedDukanViewModel.onShelfDeselected(shelf)

        // Then
        val state = approvedDukanViewModel.state.value
        assertTrue(state.products.isEmpty())
    }

    @Test
    fun `onShelfDeselected SHOULD reset product count to zero when no shelves selected`() = runTest {
        // Given
        val shelf = fakeShelves().first()
        approvedDukanViewModel.updateState {
            copy(
                selectedShelves = setOf(shelf),
                products = fakeProducts(),
                totalProducts = fakeProducts().size
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
    fun `onShelfSelected SHOULD load products from multiple selected shelves with correct count`() = runTest {
        // Given
        val shelf1 = fakeShelves()[0]
        val shelf2 = fakeShelves()[1]
        val products1 = fakeProducts().take(2)
        val products2 = fakeProducts().drop(2)
        everySuspend { productRepository.getProductsByShelfId(shelf1.id) } returns products1
        everySuspend { productRepository.getProductsByShelfId(shelf2.id) } returns products2

        // When
        approvedDukanViewModel.onShelfSelected(shelf1)
        approvedDukanViewModel.onShelfSelected(shelf2)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = approvedDukanViewModel.state.value
        assertEquals(products1.size + products2.size, state.products.size)
    }

    @Test
    fun `onShelfSelected SHOULD update product count for multiple selected shelves`() = runTest {
        // Given
        val shelf1 = fakeShelves()[0]
        val shelf2 = fakeShelves()[1]
        val products1 = fakeProducts().take(2)
        val products2 = fakeProducts().drop(2)
        everySuspend { productRepository.getProductsByShelfId(shelf1.id) } returns products1
        everySuspend { productRepository.getProductsByShelfId(shelf2.id) } returns products2

        // When
        approvedDukanViewModel.onShelfSelected(shelf1)
        approvedDukanViewModel.onShelfSelected(shelf2)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = approvedDukanViewModel.state.value
        assertEquals(products1.size + products2.size, state.totalProducts)
    }

    @Test
    fun `onShelfDeselected SHOULD clear products when all shelves deselected`() = runTest {
        // Given
        val shelf = fakeShelves().first()
        approvedDukanViewModel.updateState {
            copy(
                selectedShelves = setOf(shelf),
                products = fakeProducts(),
                totalProducts = fakeProducts().size
            )
        }

        // When
        approvedDukanViewModel.onShelfDeselected(shelf)

        // Then
        val state = approvedDukanViewModel.state.value
        assertTrue(state.products.isEmpty())
    }

    @Test
    fun `onShelfDeselected SHOULD reset product count when all shelves deselected`() = runTest {
        // Given
        val shelf = fakeShelves().first()
        approvedDukanViewModel.updateState {
            copy(
                selectedShelves = setOf(shelf),
                products = fakeProducts(),
                totalProducts = fakeProducts().size
            )
        }

        // When
        approvedDukanViewModel.onShelfDeselected(shelf)

        // Then
        val state = approvedDukanViewModel.state.value
        assertEquals(0, state.totalProducts)
    }

    @Test
    fun `onShelfDeselected SHOULD clear selected shelves when all shelves deselected`() = runTest {
        // Given
        val shelf = fakeShelves().first()
        approvedDukanViewModel.updateState {
            copy(
                selectedShelves = setOf(shelf),
                products = fakeProducts(),
                totalProducts = fakeProducts().size
            )
        }

        // When
        approvedDukanViewModel.onShelfDeselected(shelf)

        // Then
        val state = approvedDukanViewModel.state.value
        assertTrue(state.selectedShelves.isEmpty())
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

}

// ===== FAKE DATA FUNCTIONS =====

private fun fakeShelves(): List<Shelf> {
    return listOf(
        Shelf(
            id = "shelf_1",
            name = "Electronics",
            dukanId = "dukan_123"
        ),
        Shelf(
            id = "shelf_2",
            name = "Clothing",
            dukanId = "dukan_123"
        ),
        Shelf(
            id = "shelf_3",
            name = "Books",
            dukanId = "dukan_123"
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
