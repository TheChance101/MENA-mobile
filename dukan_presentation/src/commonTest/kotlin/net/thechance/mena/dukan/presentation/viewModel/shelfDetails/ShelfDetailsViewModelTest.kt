package net.thechance.mena.dukan.presentation.viewModel.shelfDetails

import androidx.lifecycle.SavedStateHandle
import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.dukan.domain.entity.Cart
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.Price
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.repository.CartRepository
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.util.PagedResult
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class ShelfDetailsViewModelTest {
    private val productRepository = mock<ProductRepository>(mode = MockMode.autofill)
    private val dukanCartRepository = mock<CartRepository>(mode = MockMode.autofill)

    private val dukanManagementRepository =
        mock<DukanManagementRepository>(mode = MockMode.autofill)

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var shelfDetailsViewModel: ShelfDetailsViewModel


    @OptIn(ExperimentalUuidApi::class)
    private val dummyProducts = listOf(
        Product(
            id = Uuid.parse("013e0bb1-6177-4430-ae08-f3a1a24f6f7d"),
            name = "Laptop",
            description = "High-end laptop",
            price = Price(
                base = 1200.0,
                final = 1200.0
            ),
            imageUrls = listOf("https://example.com/laptop.jpg"),
            createdAt = "",
            quantityInCart = 10,
            shelfId = Uuid.parse("123e4567-e89b-12d3-a456-000000000123"),
            isFavorite = false
        ),
        Product(
            id = Uuid.parse("4b8f1a92-9d2c-4bde-91ab-5c812dbb4a62"),
            name = "Mouse",
            description = "Wireless mouse",
            price = Price(
                base = 25.0,
                final = 25.0
            ),
            imageUrls = listOf("https://example.com/mouse.jpg"),
            createdAt = "",
            quantityInCart = 10,
            shelfId = Uuid.parse("123e4567-e89b-12d3-a456-000000000124"),
            isFavorite = false
        ),
        Product(
            id = Uuid.parse("a17e3c45-2fd4-4c1d-bb4a-2d5a3c739ef1"),
            name = "Keyboard",
            description = "Mechanical keyboard",
            price = Price(
                base = 75.0,
                final = 75.0
            ),
            imageUrls = listOf("https://example.com/keyboard.jpg"),
            createdAt = "",
            quantityInCart = 10,
            shelfId = Uuid.parse("123e4567-e89b-12d3-a456-000000000125"),
            isFavorite = false
        )
    )

    @OptIn(ExperimentalUuidApi::class)
    private fun dummyCart() = Cart(
        id = Uuid.parse("123e4567-e89b-12d3-a456-426614174003"),
        totalPriceBeforeDiscount = 500.0,
        discount = 0.0,
        totalPriceAfterDiscount = 500.0
    )


    @OptIn(ExperimentalUuidApi::class)
    private fun dummyDukanDetails() = Dukan(
        id = Uuid.parse("123e4567-e89b-12d3-a456-426614174003"),
        name = "Test Dukan",
        address = "123 Test Street",
        imageUrl = "https://example.com/image.png",
        coordinates = Dukan.Coordinates(latitude = 30.0, longitude = 31.0),
        color = Color(id = Uuid.random(), hexCode = "#FF0000"),
        style = Dukan.Style.WIDE_IMAGE,
        categories = emptySet(),
        status = Dukan.Status.APPROVED,
        isFavorite = false
    )

    fun createViewModel() =
        ShelfDetailsViewModel(
            productRepository = productRepository,
            defaultDispatcher = testDispatcher,
            dukanManagementRepository = dukanManagementRepository,
            dukanCartRepository = dukanCartRepository,
            savedStateHandle = savedStateHandle
        )


    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        savedStateHandle = SavedStateHandle(
            mapOf(
                "shelfName" to "Shoes",
                "dukanId" to "1",
                "shelfId" to "20"
            )
        )

        everySuspend {
            productRepository.getProductsByShelfId(any(), any(), any())
        } returns PagedResult(
            items = dummyProducts,
            currentPage = 1,
            totalItems = 3L,
            pageSize = 10,
            totalPages = 1,
        )

        shelfDetailsViewModel = createViewModel()
    }

    @AfterTest
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `init SHOULD load cart info successfully`() = runTest {
        everySuspend { dukanCartRepository.getCartInfo(any()) } returns dummyCart()

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(true, state.hasProductInCart)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init SHOULD set shelfName from savedStateHandle`() = runTest {

        //Given

        shelfDetailsViewModel.updateState {
            copy(shelfName = "Electronics")
        }
        // When
        val state = shelfDetailsViewModel.state.value

        // Then
        assertEquals("Electronics", state.shelfName)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `init SHOULD load dukan details successfully`() = runTest {
        everySuspend { dukanManagementRepository.getDukanDetailsByDukanId(any()) } returns dummyDukanDetails().copy(
            style = Dukan.Style.SMALL_IMAGE,
            color = Color(id = Uuid.random(), hexCode = "#FF0000")
        )

        shelfDetailsViewModel.updateState {
            copy(
                dukanStyle = ShelfDetailsUiState.Style.SMALL_IMAGE,
                dukancolor = 0xFFFFFF
            )
        }
        advanceUntilIdle()

        shelfDetailsViewModel.state.test {
            val state = awaitItem()
            assertTrue(state.dukanStyle.name.isNotEmpty())
            assertTrue(state.dukancolor != 0L)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init SHOULD load products from repository`() = runTest {
        // When
        advanceUntilIdle()
        val state = shelfDetailsViewModel.state.value

        // Then
        assertTrue(state.productsShelf.asSnapshot().isNotEmpty())
    }

    @Test
    fun `init SHOULD load correct number of products`() = runTest {
        // When
        advanceUntilIdle()
        val state = shelfDetailsViewModel.state.value

        // Then
        assertEquals(3, state.productsShelf.asSnapshot().size)
    }


    @Test
    fun `products SHOULD have correct IDs`() = runTest {
        // When
        advanceUntilIdle()
        val state = shelfDetailsViewModel.state.value

        // Then
        val productIds = state.productsShelf.asSnapshot().map { it.id }
        assertTrue(productIds.contains("013e0bb1-6177-4430-ae08-f3a1a24f6f7d"))
        assertTrue(productIds.contains("4b8f1a92-9d2c-4bde-91ab-5c812dbb4a62"))
        assertTrue(productIds.contains("a17e3c45-2fd4-4c1d-bb4a-2d5a3c739ef1"))
    }

    @Test
    fun `products SHOULD have correct names`() = runTest {
        // When
        advanceUntilIdle()
        val state = shelfDetailsViewModel.state.value

        // Then
        val productNames = state.productsShelf.asSnapshot().map { it.name }
        assertTrue(productNames.contains("Laptop"))
        assertTrue(productNames.contains("Mouse"))
        assertTrue(productNames.contains("Keyboard"))
    }

    @Test
    fun `products SHOULD have correct prices`() = runTest {
        // When
        advanceUntilIdle()
        val productsShelfs = shelfDetailsViewModel.state.value.productsShelf.asSnapshot()

        // Then
        assertEquals(1200.0, productsShelfs[0].basePrice)
        assertEquals(25.0, productsShelfs[1].basePrice)
        assertEquals(75.0, productsShelfs[2].basePrice)
    }

    @Test
    fun `products SHOULD have correct descriptions`() = runTest {
        // When
        advanceUntilIdle()
        val productsShelfs = shelfDetailsViewModel.state.value.productsShelf.asSnapshot()

        // Then
        assertEquals("High-end laptop", productsShelfs[0].description)
        assertEquals("Wireless mouse", productsShelfs[1].description)
        assertEquals("Mechanical keyboard", productsShelfs[2].description)
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
    fun `onAddToCartClicked SHOULD toggle product cart to product quantity and make request to add product`() =
        runTest {
            // Given
            val productId = "1"
            val quantity = 1

            everySuspend { dukanCartRepository.addProductQuantity(any()) } returns Unit

            //When
            shelfDetailsViewModel.onAddToCartClicked(
                productId,
                productQuantity = quantity,
            )
            advanceUntilIdle()
            //Then
            verifySuspend {
                dukanCartRepository.addProductQuantity(any())
            }

        }

    @Test
    fun `onPlusClicked SHOULD increase product quantity in cart `() = runTest {

        //Given
        val productId = "1"
        val quantity = 20
        everySuspend { dukanCartRepository.addProductQuantity(any()) } returns Unit

        //When
        shelfDetailsViewModel.onPlusClicked(productId, productQuantity = quantity)

        advanceUntilIdle()
        //Then
        verifySuspend {
            dukanCartRepository.updateProductQuantity(any())
        }
    }


    @Test
    fun `onPlusClicked SHOULD update hasProductInCart to true`() = runTest {
        val productId = "1"
        val quantity = 1

        shelfDetailsViewModel.onPlusClicked(
            productId,
            productQuantity = quantity,
        )

        advanceUntilIdle()

        shelfDetailsViewModel.state.test {
            val state = awaitItem()
            assertEquals(true, state.hasProductInCart)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onMinusClicked SHOULD decrease product quantity in cart `() = runTest {

        //Given

        val productId = "1"
        val quantity = 10

        everySuspend { dukanCartRepository.updateProductQuantity(any()) } returns Unit

        //When
        shelfDetailsViewModel.onMinusClicked(productId, productQuantity = quantity)

        advanceUntilIdle()
        //Then
        verifySuspend { dukanCartRepository.updateProductQuantity(any()) }
    }

    @Test
    fun `onMinusClicked SHOULD delete product when quantity is 1`() = runTest {

        //Given
        val productId = "1"
        val quantity = 0

        everySuspend { dukanCartRepository.deleteProductFromCart(any(), any()) } returns Unit

        //When
        shelfDetailsViewModel.onMinusClicked(productId, productQuantity = quantity)

        advanceUntilIdle()
        //Then
        verifySuspend { dukanCartRepository.deleteProductFromCart(any(), any()) }
    }

    @Test
    fun `onDismissSnackBar SHOULD hide snack bar`() = runTest {

        shelfDetailsViewModel.onDismissSnackBar()

        assertTrue(shelfDetailsViewModel.state.value.snackBarState == null)
    }

    @Test
    fun `onErrorUpdateProductQuantity SHOULD show error snackbar when NoInternetException thrown`() =
        runTest {
            // Given
            val productId = "1"
            val quantity = 5

            everySuspend { dukanCartRepository.addProductQuantity(any()) } throws NoInternetException()

            // When
            shelfDetailsViewModel.onAddToCartClicked(productId, productQuantity = quantity)
            advanceUntilIdle()
            // Then
            shelfDetailsViewModel.state.test {
                val state = awaitItem()
                assertTrue(state.snackBarState != null)
            }
        }

    @Test
    fun `onErrorUpdateProductQuantity SHOULD show error snackbar when anyException thrown`() =
        runTest {
            // Given
            val productId = "1"
            val quantity = 5

            everySuspend { dukanCartRepository.addProductQuantity(any()) } throws Exception()

            // When
            shelfDetailsViewModel.onAddToCartClicked(productId, productQuantity = quantity)
            advanceUntilIdle()

            // Then
            shelfDetailsViewModel.state.test {
                val state = awaitItem()
                assertTrue(state.snackBarState != null)
            }
        }

}