package net.thechance.mena.dukan.presentation.viewModel.dukanDetails

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
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.repository.CartRepository
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.domain.util.PagedResult
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class DukanDetailsViewModelTest {

    private val dukanManagementRepository =
        mock<DukanManagementRepository>(mode = MockMode.autofill)
    private val shelfRepository = mock<ShelfRepository>(mode = MockMode.autofill)
    private val productRepository = mock<ProductRepository>(mode = MockMode.autofill)
    private val dukanCartRepository = mock<CartRepository>(mode = MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var dukanDetailsViewModel: DukanDetailsViewModel

    @OptIn(ExperimentalUuidApi::class)
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        savedStateHandle = SavedStateHandle(
            mapOf("dukanId" to dummyDukanDetails().id.toString())
        )
        everySuspend { dukanManagementRepository.getDukanDetailsByDukanId(any()) } returns dummyDukanDetails()
        everySuspend {
            shelfRepository.getShelvesByDukanId(any(), any(), any())
        } returns PagedResult(
            items = dummyShelves(),
            currentPage = 1,
            totalItems = 3L,
            totalPages = 1,
            pageSize = 1
        )

        everySuspend {
            productRepository.getProductsByShelfId(any(), any(), any())
        } returns PagedResult(
            items = emptyList(),
            currentPage = 1,
            totalItems = 0L,
            pageSize = 10,
            totalPages = 1
        )

        dukanDetailsViewModel = createViewModel()
    }

    @AfterTest
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `init SHOULD load dukan details successfully`() = runTest {
        everySuspend { dukanManagementRepository.getDukanDetailsByDukanId(any()) } returns dummyDukanDetails()
        everySuspend {
            shelfRepository.getShelvesByDukanId(any(), any(), any())
        } returns PagedResult(
            items = dummyShelves(),
            currentPage = 1,
            totalItems = 3L,
            totalPages = 1,
            pageSize = 10
        )
        everySuspend {
            productRepository.getProductsByShelfId(any(), any(), any())
        } returns PagedResult(
            items = fakeProducts(),
            currentPage = 1,
            totalItems = 1L,
            pageSize = 10,
            totalPages = 1
        )

        val viewModel = createViewModel()
        advanceUntilIdle()
        val shelvesSnapshot = viewModel.state.value.shelves.asSnapshot()
        assertTrue(shelvesSnapshot.isNotEmpty(), "Expected shelves not to be empty")
    }

    @Test
    fun `init SHOULD set isDukanInfoLoading to false after successful load`() = runTest {
        advanceUntilIdle()
        val state = dukanDetailsViewModel.state.value

        assertFalse(state.isDukanInfoLoading)
    }

    @Test
    fun `init SHOULD set isDukanInfoLoading to false when details loading fails`() = runTest {
        // Given
        everySuspend { dukanManagementRepository.getDukanDetailsByDukanId(any()) } throws Exception(
            "Network Error"
        )

        // When
        val errorViewModel = createViewModel()
        advanceUntilIdle()
        val state = errorViewModel.state.value

        // Then
        assertFalse(state.isDukanInfoLoading)
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
    fun `init SHOULD set shelves state to EMPTY when no shelves returned`() = runTest {
        // Given
        everySuspend {
            shelfRepository.getShelvesByDukanId(any(), any(), any())
        } returns PagedResult(
            items = emptyList(),
            currentPage = 1,
            totalItems = 0L,
            totalPages = 1,
            pageSize = 10
        )

        // When
        val viewModel = createViewModel()
        advanceUntilIdle()
        val state = viewModel.state.value

        val shelvesList = state.shelves.asSnapshot()
        assertTrue(shelvesList.isEmpty())
    }

    @Test
    fun `onShelfClicked SHOULD reset productsShelf to empty`() = runTest {
        // Given
        advanceUntilIdle()
        val newShelfId = "shelf_2"

        // When
        dukanDetailsViewModel.onShelfClicked(newShelfId)
        val state = dukanDetailsViewModel.state.value

        // Then
        assertTrue(state.productsShelf.asSnapshot().isEmpty())
    }

    @Test
    fun `onShelfClicked with WIDE_IMAGE style SHOULD load products from repository`() = runTest {
        // Given
        advanceUntilIdle()
        val targetShelfId = "shelf_1"
        setupProductsForShelf(targetShelfId)

        // When
        dukanDetailsViewModel.onShelfClicked(targetShelfId)
        advanceUntilIdle()

        // Then
        val state = dukanDetailsViewModel.state.value
        assertEquals(fakeProducts().map { it.toUiState() }, state.productsShelf.asSnapshot())
    }

    @Test
    fun `onShelfClicked SHOULD update selected shelf id in state`() = runTest {
        // Given
        advanceUntilIdle()
        val newShelfId = "shelf_2"

        // When
        dukanDetailsViewModel.onShelfClicked(newShelfId)
        val state = dukanDetailsViewModel.state.value

        // Then
        assertEquals(newShelfId, state.shelfIdSelected)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `onShelfClicked SHOULD load correct number of products`() = runTest {
        // Given
        val targetShelfId = Uuid.parse("123e4567-e89b-12d3-a456-426614174003").toString()
        setupProductsForShelf(targetShelfId)

        // When
        dukanDetailsViewModel.onShelfClicked(targetShelfId)
        advanceUntilIdle()

        // Then
        dukanDetailsViewModel.state.test {
            assertEquals(1, awaitItem().productsShelf.asSnapshot().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `onShelfClicked SHOULD load product with correct ID`() = runTest {
        // Given
        val targetShelfId = Uuid.parse("123e4567-e89b-12d3-a456-426614174003").toString()
        setupProductsForShelf(targetShelfId)

        // When
        dukanDetailsViewModel.onShelfClicked(targetShelfId)
        advanceUntilIdle()

        // Then
        dukanDetailsViewModel.state.test {
            assertEquals(
                "123e4567-e89b-12d3-a456-426614174003",
                awaitItem().productsShelf.asSnapshot().first().id
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `onShelfClicked SHOULD load product with correct name`() = runTest {
        // Given
        val targetShelfId = Uuid.parse("123e4567-e89b-12d3-a456-426614174003").toString()
        setupProductsForShelf(targetShelfId)

        // When
        dukanDetailsViewModel.onShelfClicked(targetShelfId)
        advanceUntilIdle()

        // Then
        dukanDetailsViewModel.state.test {
            assertEquals("Laptop", awaitItem().productsShelf.asSnapshot().first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getProductsPager SHOULD return the same cached pager instance for the same shelfId`() =
        runTest {
            // When
            val pagerInstance1 = dukanDetailsViewModel.onShelfClicked("shelf_1")
            val pagerInstance2 = dukanDetailsViewModel.onShelfClicked("shelf_1")

            // Then
            assertTrue(pagerInstance1 === pagerInstance2)
        }

    @Test
    fun `when onBackClicked SHOULD emit NavigateBack effect`() = runTest {
        // Then
        dukanDetailsViewModel.effect.test {
            // When
            dukanDetailsViewModel.onBackClicked()
            assertEquals(DukanDetailsEffects.NavigateBackWithDukanId, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when onViewDukanOnMapClicked SHOULD emit correct navigation effect`() = runTest {
        // Given
        val lat = 28.0
        val lon = 29.0

        // Then
        dukanDetailsViewModel.effect.test {
            // When
            dukanDetailsViewModel.onViewDukanOnMapClicked(lat, lon)
            assertEquals(DukanDetailsEffects.NavigateToViewDukanOnMap(lat, lon), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `onFavoriteDukanClicked SHOULD toggle isFavorite in state when repository call succeeds`() =
        runTest {
            // Given
            advanceUntilIdle()
            val initialState = dukanDetailsViewModel.state.value
            val initialFavorite = initialState.dukanInfo.isFavorite
            val dukanId = initialState.dukanInfo.dukanId

            everySuspend {
                dukanManagementRepository.updateFavoriteDukanStatus(dukanId)
            }

            // When
            dukanDetailsViewModel.onFavoriteDukanClicked(dukanId)
            advanceUntilIdle()

            // Then
            val updatedState = dukanDetailsViewModel.state.value
            assertEquals(!initialFavorite, updatedState.dukanInfo.isFavorite)
        }

    @Test
    fun `onAddToCartClicked SHOULD toggle product cart to product quantity and make request to add product`() =
        runTest {
            // Given
            val productId = "1"
            val quantity = 1

            everySuspend { dukanCartRepository.addProductQuantity(any()) } returns Unit

            //When
            dukanDetailsViewModel.onAddToCartClicked(
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
        dukanDetailsViewModel.onPlusClicked(productId, productQuantity = quantity)

        advanceUntilIdle()
        //Then
        verifySuspend {
            dukanCartRepository.updateProductQuantity(any())
        }
    }

    @Test
    fun `onMinusClicked SHOULD decrease product quantity in cart `() = runTest {

        //Given

        val productId = "1"
        val quantity = 10

        everySuspend { dukanCartRepository.updateProductQuantity(any()) } returns Unit

        //When
        dukanDetailsViewModel.onMinusClicked(productId, productQuantity = quantity)

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
        dukanDetailsViewModel.onMinusClicked(productId, productQuantity = quantity)

        advanceUntilIdle()
        //Then
        verifySuspend { dukanCartRepository.deleteProductFromCart(any(), any()) }
    }

    @Test
    fun `onDismissSnackBar SHOULD hide snack bar`() = runTest {

        dukanDetailsViewModel.onDismissSnackBar()

        assertTrue(dukanDetailsViewModel.state.value.snackBarState == null)
    }

    @Test
    fun `onErrorUpdateProductQuantity SHOULD show error snackbar when NoInternetException thrown`() =
        runTest {
            // Given
            val productId = "1"
            val quantity = 5

            everySuspend { dukanCartRepository.addProductQuantity(any()) } throws NoInternetException()

            // When
            dukanDetailsViewModel.onAddToCartClicked(productId, productQuantity = quantity)
            advanceUntilIdle()
            // Then
            dukanDetailsViewModel.state.test {
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
            dukanDetailsViewModel.onAddToCartClicked(productId, productQuantity = quantity)
            advanceUntilIdle()

            // Then
            dukanDetailsViewModel.state.test {
                val state = awaitItem()
                assertTrue(state.snackBarState != null)
            }
        }


    private fun createViewModel() = DukanDetailsViewModel(
        dukanManagementRepository = dukanManagementRepository,
        shelfRepository = shelfRepository,
        productRepository = productRepository,
        defaultDispatcher = testDispatcher,
        savedStateHandle = savedStateHandle,
        dukanCartRepository = dukanCartRepository
    )

    private fun setupProductsForShelf(shelfId: String) {
        everySuspend {
            productRepository.getProductsByShelfId(shelfId = shelfId, page = any(), size = any())
        } returns PagedResult(
            items = fakeProducts(),
            currentPage = 1,
            totalItems = 1L,
            pageSize = 10,
            totalPages = 1
        )
    }
}

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
    isFavorite = true,
    address = "123 Test Street",
    imageUrl = "https://example.com/image.png",
    coordinates = Dukan.Coordinates(latitude = 30.0, longitude = 31.0),
    color = Color(id = Uuid.random(), hexCode = "#FF0000"),
    style = Dukan.Style.WIDE_IMAGE,
    categories = emptySet(),
    status = Dukan.Status.APPROVED,
)

@OptIn(ExperimentalUuidApi::class)
private fun dummyShelves() = listOf(
    Shelf(id = Uuid.parse("123e4567-e89b-12d3-a456-426614174003"), name = "Electronics"),
    Shelf(id = Uuid.parse("123e4567-e89b-12d3-a456-426614174002"), name = "Clothing"),
    Shelf(id = Uuid.parse("123e4567-e89b-12d3-a456-426614174001"), name = "Books")
)

@OptIn(ExperimentalUuidApi::class)
private fun fakeProducts(): List<Product> = listOf(
    Product(
        id = Uuid.parse("123e4567-e89b-12d3-a456-426614174003"),
        name = "Laptop",
        description = "A cool laptop",
        price = Price(
            base = 1200.0,
            final = 1200.0
        ),
        imageUrls = emptyList(),
        createdAt = "2025-10-10T12:00:00Z",
        quantityInCart = 10,
        shelfId = Uuid.parse("123e4567-e89b-12d3-a456-000000000123"),
        isFavorite = false
    )
)