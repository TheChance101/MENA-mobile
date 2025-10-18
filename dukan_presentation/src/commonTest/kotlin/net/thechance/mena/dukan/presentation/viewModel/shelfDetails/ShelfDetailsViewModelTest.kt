package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
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
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.util.PagedResult
import net.thechance.mena.dukan.presentation.navigation.SavedStateHandleArgs.DUKAN_COLOR
import net.thechance.mena.dukan.presentation.navigation.SavedStateHandleArgs.DUKAN_STYLE
import net.thechance.mena.dukan.presentation.navigation.SavedStateHandleArgs.SHELF_ID
import net.thechance.mena.dukan.presentation.navigation.SavedStateHandleArgs.SHELF_NAME
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ShelfDetailsViewModelTest {
    private val productRepository = mock<ProductRepository>(mode = MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var shelfDetailsViewModel: ShelfDetailsViewModel

    private fun createViewModel(handle: SavedStateHandle = savedStateHandle) =
        ShelfDetailsViewModel(
            productRepository = productRepository,
            defaultDispatcher = testDispatcher,
            savedStateHandle = handle
        )

    private val dummyProducts = listOf(
        Product(
            id = "product_1",
            name = "Laptop",
            description = "High-end laptop",
            price = 1200.0,
            imageUrls = listOf("https://example.com/laptop.jpg"),
            createdAt = ""
        ),
        Product(
            id = "product_2",
            name = "Mouse",
            description = "Wireless mouse",
            price = 25.0,
            imageUrls = listOf("https://example.com/mouse.jpg"),
            createdAt = ""
        ),
        Product(
            id = "product_3",
            name = "Keyboard",
            description = "Mechanical keyboard",
            price = 75.0,
            imageUrls = listOf("https://example.com/keyboard.jpg"),
            createdAt = ""
        )
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle(
            mapOf(
                SHELF_ID to "shelf_123",
                SHELF_NAME to "Electronics",
                DUKAN_STYLE to "WIDE_IMAGE",
                DUKAN_COLOR to 0xFF0000L
            )
        )

        everySuspend {
            productRepository.getProductsByShelfId(any(), any(), any())
        } returns PagedResult(
            items = dummyProducts,
            currentPage = 1,
            totalItems = 3L,
            totalPages = 1
        )

        shelfDetailsViewModel = createViewModel()
    }

    @AfterTest
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init SHOULD set shelfName from savedStateHandle`() = runTest {
        // When
        val state = shelfDetailsViewModel.state.value

        // Then
        assertEquals("Electronics", state.shelfName)
    }

    @Test
    fun `init SHOULD set dukanStyle from savedStateHandle`() = runTest {
        // When
        val state = shelfDetailsViewModel.state.value

        // Then
        assertEquals(ShelfDetailsUiState.Style.WIDE_IMAGE, state.dukanStyle)
    }

    @Test
    fun `init SHOULD set dukanColor from savedStateHandle`() = runTest {
        // When
        val state = shelfDetailsViewModel.state.value

        // Then
        assertEquals(0xFF0000L, state.dukancolor)
    }

    @Test
    fun `init SHOULD set productsState to LOADING initially`() = runTest {
        // When
        val state = shelfDetailsViewModel.state.value

        // Then
        assertEquals(ShelfDetailsUiState.ProductsState.LOADING, state.productsState)
    }

    @Test
    fun `init SHOULD load products from repository`() = runTest {
        // When
        advanceUntilIdle()
        val state = shelfDetailsViewModel.state.value

        // Then
        assertEquals(ShelfDetailsUiState.ProductsState.LOADED, state.productsState)
        assertTrue(state.productsShelf.items.isNotEmpty())
    }

    @Test
    fun `init SHOULD load correct number of products`() = runTest {
        // When
        advanceUntilIdle()
        val state = shelfDetailsViewModel.state.value

        // Then
        assertEquals(3, state.productsShelf.items.size)
    }


    @Test
    fun `products SHOULD have correct IDs`() = runTest {
        // When
        advanceUntilIdle()
        val state = shelfDetailsViewModel.state.value

        // Then
        val productIds = state.productsShelf.items.map { it.id }
        assertTrue(productIds.contains("product_1"))
        assertTrue(productIds.contains("product_2"))
        assertTrue(productIds.contains("product_3"))
    }

    @Test
    fun `products SHOULD have correct names`() = runTest {
        // When
        advanceUntilIdle()
        val state = shelfDetailsViewModel.state.value

        // Then
        val productNames = state.productsShelf.items.map { it.name }
        assertTrue(productNames.contains("Laptop"))
        assertTrue(productNames.contains("Mouse"))
        assertTrue(productNames.contains("Keyboard"))
    }

    @Test
    fun `products SHOULD have correct prices`() = runTest {
        // When
        advanceUntilIdle()
        val state = shelfDetailsViewModel.state.value

        // Then
        assertEquals(1200.0, state.productsShelf.items[0].price)
        assertEquals(25.0, state.productsShelf.items[1].price)
        assertEquals(75.0, state.productsShelf.items[2].price)
    }

    @Test
    fun `products SHOULD have correct descriptions`() = runTest {
        // When
        advanceUntilIdle()
        val state = shelfDetailsViewModel.state.value

        // Then
        assertEquals("High-end laptop", state.productsShelf.items[0].description)
        assertEquals("Wireless mouse", state.productsShelf.items[1].description)
        assertEquals("Mechanical keyboard", state.productsShelf.items[2].description)
    }

    @Test
    fun `products SHOULD initialize with zero cart quantity`() = runTest {
        // When
        advanceUntilIdle()
        val state = shelfDetailsViewModel.state.value

        // Then
        state.productsShelf.items.forEach { product ->
            assertEquals(0, product.inCartQuantity)
        }
    }

    @Test
    fun `onBackClicked SHOULD emit NavigateBack effect`() = runTest {
        // Then
        shelfDetailsViewModel.effect.test {
            // When
            shelfDetailsViewModel.onBackClicked()
            assertEquals(ShelfDetailsEffects.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onAddToCartClick SHOULD set inCartQuantity to 1 for specific product`() = runTest {
        // Given
        advanceUntilIdle()
        val productId = "product_1"

        // When
        shelfDetailsViewModel.onAddToCartClick(productId)
        val state = shelfDetailsViewModel.state.value

        // Then
        val product = state.productsShelf.items.find { it.id == productId }
        assertEquals(1, product?.inCartQuantity)
    }
}