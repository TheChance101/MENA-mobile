package net.thechance.mena.dukan.presentation.viewModel.productDetails

import androidx.lifecycle.SavedStateHandle
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
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.repository.ProductRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailsViewModelTest {

    private val productRepository = mock<ProductRepository>(mode = MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var productDetailsViewModel: ProductDetailsViewModel

    @OptIn(ExperimentalUuidApi::class)
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle(
            mapOf("productId" to dummyProductDetails().id.toString())
        )
        everySuspend { productRepository.getProductDetails(any()) } returns dummyProductDetails()

        productDetailsViewModel = createViewModel()
    }

    @AfterTest
    fun cleanup() {
        Dispatchers.resetMain()
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
        assertEquals(dummyProductDetails().price, state.product.price)
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

        productDetailsViewModel.onSecondaryImageClicked(newImageUrl)

        val state = productDetailsViewModel.state.value
        assertEquals(newImageUrl, state.selectedImageUrl)
    }

    private fun createViewModel() = ProductDetailsViewModel(
        productRepository = productRepository,
        defaultDispatcher = testDispatcher,
        savedStateHandle = savedStateHandle
    )
}

@OptIn(ExperimentalUuidApi::class)
private fun dummyProductDetails(): Product = Product(
    id = Uuid.parse("a1b2c3d4-e5f6-7890-1234-567890abcdef"),
    name = "Vintage T-Shirt",
    description = "A very cool vintage t-shirt.",
    price = 29.99,
    imageUrls = listOf(
        "http://example.com/image1.jpg",
        "http://example.com/image2.jpg"
    ),
    createdAt = "2025-10-31T12:00:00Z"
)