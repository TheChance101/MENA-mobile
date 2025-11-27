package net.thechance.mena.dukan.presentation.viewModel.productDetails

import androidx.lifecycle.SavedStateHandle
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
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.no_internet_connection
import net.thechance.mena.dukan.domain.entity.Cart
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.Price
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.repository.CartRepository
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailsViewModelTest {

    private val productRepository = mock<ProductRepository>(mode = MockMode.autofill)
    private val dukanCartRepository = mock<CartRepository>(mode = MockMode.autofill)
    private val dukanManagementRepository =
        mock<DukanManagementRepository>(mode = MockMode.autofill)

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var productDetailsViewModel: ProductDetailsViewModel

    @OptIn(ExperimentalUuidApi::class)
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle(
            mapOf(
                "productId" to dummyProductDetails().id.toString(),
                "dukanId" to "123"
            )
        )
        everySuspend { productRepository.getProductDetails(any()) } returns dummyProductDetails()

        productDetailsViewModel = createViewModel()
    }

    @AfterTest
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `init SHOULD load dukan details successfully`() = runTest {
        everySuspend { dukanManagementRepository.getDukanDetailsByDukanId(any()) } returns dummyDukanDetails()

        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(parseHexColor(dummyDukanDetails().color.hexCode), state.dukanColor)
            cancelAndIgnoreRemainingEvents()
        }
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
    fun `init SHOULD set isLoading to false after successful load`() = runTest {
        advanceUntilIdle()

        val state = productDetailsViewModel.state.value
        assertFalse(state.isLoading)
    }

    @Test
    fun `init SHOULD set errorState to null after successful load`() = runTest {
        advanceUntilIdle()

        val state = productDetailsViewModel.state.value
        assertNull(state.errorState)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `init SHOULD load correct product ID after successful load`() = runTest {
        advanceUntilIdle()

        val state = productDetailsViewModel.state.value
        assertEquals(dummyProductDetails().id.toString(), state.product.id)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `init SHOULD load correct product name after successful load`() = runTest {
        advanceUntilIdle()

        val state = productDetailsViewModel.state.value
        assertEquals(dummyProductDetails().name, state.product.name)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `init SHOULD load correct product price after successful load`() = runTest {
        advanceUntilIdle()

        val state = productDetailsViewModel.state.value
        assertEquals(dummyProductDetails().price.base, state.product.basePrice)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `init SHOULD load correct product description after successful load`() = runTest {
        advanceUntilIdle()

        val state = productDetailsViewModel.state.value
        assertEquals(dummyProductDetails().description, state.product.description)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `init SHOULD load correct product images after successful load`() = runTest {
        advanceUntilIdle()

        val state = productDetailsViewModel.state.value
        assertEquals(dummyProductDetails().imageUrls, state.product.images)
    }

    @Test
    fun `init SHOULD set first image as selected image URL after successful load`() = runTest {
        advanceUntilIdle()

        val state = productDetailsViewModel.state.value
        assertEquals(dummyProductDetails().imageUrls.first(), state.selectedImageUrl)
    }

    @Test
    fun `init SHOULD set isLoading to false when load fails`() = runTest {
        everySuspend { productRepository.getProductDetails(any()) } throws Exception("Network Error")

        val errorViewModel = createViewModel()
        advanceUntilIdle()
        val state = errorViewModel.state.value

        assertFalse(state.isLoading)
    }

    @Test
    fun `init SHOULD set errorState to non-null when load fails`() = runTest {
        val networkError = Exception("Network Error")
        everySuspend { productRepository.getProductDetails(any()) } throws networkError

        val errorViewModel = createViewModel()
        advanceUntilIdle()
        val state = errorViewModel.state.value

        assertNotNull(state.errorState)
        assertEquals(networkError, state.errorState)
    }

    @Test
    fun `when onBackClicked SHOULD emit NavigateBack effect`() = runTest {
        productDetailsViewModel.effect.test {
            productDetailsViewModel.onBackClicked()
            assertEquals(ProductDetailsEffects.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSecondaryImageClicked SHOULD update selectedImageUrl`() = runTest {
        val newImageUrl = "http://example.com/image2.jpg"
        val currentSelected = productDetailsViewModel.state.value.selectedImageUrl

        productDetailsViewModel.onSecondaryImageClicked(
            imageUrl = newImageUrl,
            selectedImageUrl = currentSelected
        )

        val state = productDetailsViewModel.state.value
        assertEquals(newImageUrl, state.selectedImageUrl)
    }

    @Test
    fun `onPlusClicked increases product quantity`() = runTest {
        // Given
        productDetailsViewModel.updateState { copy(product.copy(inCartQuantity = 3)) }

        // When
        productDetailsViewModel.onPlusClicked("10")

        advanceUntilIdle()

        // Then
        val updatedQuantity = productDetailsViewModel.state.value.product.inCartQuantity
        assertEquals(4, updatedQuantity)
    }

    @Test
    fun `onMinusClicked decreases product quantity when greater than one`() = runTest {
        // Given
        productDetailsViewModel.updateState { copy(product.copy(inCartQuantity = 3)) }

        // When
        productDetailsViewModel.onMinusClicked("10")
        advanceUntilIdle()

        // Then
        val updatedQuantity = productDetailsViewModel.state.value.product.inCartQuantity
        assertEquals(2, updatedQuantity)
    }

    @Test
    fun `onMinusClicked keeps quantity same when equal to 0`() = runTest {
        // Given
        productDetailsViewModel.updateState { copy(product.copy(inCartQuantity = 0)) }

        // When
        productDetailsViewModel.onMinusClicked("10")
        advanceUntilIdle()

        // Then
        val updatedQuantity = productDetailsViewModel.state.value.product.inCartQuantity
        assertEquals(0, updatedQuantity)
    }

    @Test
    fun `onAddToCartClicked SHOULD update product quantity when product exist`() =
        runTest {
            // Given
            val productId = "1"

            productDetailsViewModel.updateState {
                copy(isFirstQuantityOne = false, isNotSameQuantity = true)
            }

            everySuspend { dukanCartRepository.updateProductQuantity(any()) } returns Unit

            //When
            productDetailsViewModel.onAddToCartClicked(
                productId,
            )
            advanceUntilIdle()
            //Then
            verifySuspend {
                dukanCartRepository.updateProductQuantity(any())
            }
            assertTrue(productDetailsViewModel.state.value.snackBarState != null)


        }

    @Test
    fun `onAddToCartClicked SHOULD add new product when quantity equal 0`() =
        runTest {
            // Given
            val productId = "1"
            productDetailsViewModel.updateState {
                copy(isFirstQuantityOne = true, isNotSameQuantity = true)
            }

            everySuspend { dukanCartRepository.addProductQuantity(any()) } returns Unit

            //When
            productDetailsViewModel.onAddToCartClicked(
                productId,
            )
            advanceUntilIdle()
            //Then
            verifySuspend {
                dukanCartRepository.addProductQuantity(any())
            }
            assertTrue(productDetailsViewModel.state.value.snackBarState != null)

        }

    @Test
    fun `onDismissSnackBar clears snackbar state`() = runTest {

        // When
        productDetailsViewModel.onDismissSnackBar()

        // Then
        assertTrue(productDetailsViewModel.state.value.snackBarState == null)
    }

    @Test
    fun `onErrorUpdateProductQuantity SHOULD show error snackbar when NoInternetException thrown`() =
        runTest {
            // Given
            val productId = "1"
            productDetailsViewModel.updateState {
                copy(
                    isNotSameQuantity = true,
                    isFirstQuantityOne = false
                )
            }

            everySuspend { dukanCartRepository.updateProductQuantity(any()) } throws NoInternetException()

            // When
            productDetailsViewModel.onAddToCartClicked(productId)
            advanceUntilIdle()

            // Then
            val state = productDetailsViewModel.state.value
            assertEquals(Res.string.no_internet_connection, state.snackBarState?.message)
            assertEquals(SnackBarType.ERROR, state.snackBarState?.snackBarType)
        }

    @OptIn(ExperimentalUuidApi::class)
    private val testProductId = dummyProductDetails().id.toString()


    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `onToggleProductToFavoriteClicked_whenAddingToFavorites_SHOULD callRepository`() = runTest {
        // Given
        productDetailsViewModel.updateState { copy(isFavorite = false) }
        everySuspend { productRepository.toggleProductToFavorites(testProductId) } returns Unit

        // When
        productDetailsViewModel.onToggleProductToFavoriteClicked()
        advanceUntilIdle()

        // Then
        verifySuspend { productRepository.toggleProductToFavorites(testProductId) }
    }


    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `onToggleProductToFavoriteClicked_whenRemovingFromFavorites_SHOULD callRepository`() =
        runTest {
            // Given
            productDetailsViewModel.updateState { copy(isFavorite = true) }
            everySuspend { productRepository.toggleProductToFavorites(testProductId) } returns Unit

            // When
            productDetailsViewModel.onToggleProductToFavoriteClicked()
            advanceUntilIdle()

            // Then
            verifySuspend { productRepository.toggleProductToFavorites(testProductId) }
        }


    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `onToggleProductToFavoriteClicked_whenErrorOccurs_SHOULD callRepository`() = runTest {
        // Given
        val networkError = NoInternetException()
        productDetailsViewModel.updateState { copy(isFavorite = false) }
        everySuspend { productRepository.toggleProductToFavorites(testProductId) } throws networkError

        // When
        productDetailsViewModel.onToggleProductToFavoriteClicked()
        advanceUntilIdle()

        // Then
        verifySuspend { productRepository.toggleProductToFavorites(testProductId) }
    }

    @Test
    fun `onToggleProductToFavoriteClicked_whenErrorOccurs_SHOULD keep the change in ui`() =
        runTest {
            // Given
            val networkError = NoInternetException()
            productDetailsViewModel.updateState { copy(isFavorite = false) }
            everySuspend { productRepository.toggleProductToFavorites(any()) } throws networkError

            // When
            productDetailsViewModel.onToggleProductToFavoriteClicked()
            advanceUntilIdle()

            // Then
            val state = productDetailsViewModel.state.value
            assertTrue(state.isFavorite)
        }


    private fun createViewModel() = ProductDetailsViewModel(
        productRepository = productRepository,
        defaultDispatcher = testDispatcher,
        savedStateHandle = savedStateHandle,
        dukanCartRepository = dukanCartRepository,
        dukanManagementRepository = dukanManagementRepository
    )
}

@OptIn(ExperimentalUuidApi::class)
private fun dummyCart() = Cart(
    id = Uuid.parse("123e4567-e89b-12d3-a456-426614174003"),
    totalPriceBeforeDiscount = 500.0,
    discount = 0.0,
    totalPriceAfterDiscount = 500.0
)

@OptIn(ExperimentalUuidApi::class)
private fun dummyProductDetails(): Product = Product(
    id = Uuid.parse("a1b2c3d4-e5f6-7890-1234-567890abcdef"),
    name = "Vintage T-Shirt",
    description = "A very cool vintage t-shirt.",
    price = Price(
        base = 29.99,
        final = 29.99
    ),
    imageUrls = listOf(
        "http://example.com/image1.jpg",
        "http://example.com/image2.jpg"
    ),
    createdAt = "2025-10-31T12:00:00Z",
    quantityInCart = 10,
    shelfId = Uuid.parse("123e4567-e89b-12d3-a456-000000000124"),
    isFavorite = false
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