package net.thechance.mena.dukan.presentation.viewModel.createProduct

import androidx.compose.ui.graphics.ImageBitmap
import app.cash.turbine.test
import com.attafitamim.krop.core.images.ImageSrc
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.error_general
import mena.dukan_presentation.generated.resources.error_image_max_limit
import mena.dukan_presentation.generated.resources.error_image_size
import mena.dukan_presentation.generated.resources.error_price_invalid
import mena.dukan_presentation.generated.resources.error_upload_failed
import net.thechance.mena.dukan.domain.entity.Price
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.presentation.component.product.productImage.ProductImageState
import net.thechance.mena.dukan.presentation.util.file.ImageFile
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class CreateProductViewModelTest {

    private val productRepository = mock<ProductRepository>()
    private val shelfRepository = mock<ShelfRepository>()
    private lateinit var viewModel: CreateProductViewModel
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)


    @BeforeTest
    fun setUp() {
        viewModel = CreateProductViewModel(productRepository, shelfRepository, dispatcher)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `getShelves Should update emit success state`() = scope.runTest {
        val shelves = listOf(Shelf(Uuid.random(), "Shelf1"), Shelf(Uuid.random(), "Shelf2"))
        everySuspend { shelfRepository.getMyDukanShelves() } returns shelves

        viewModel = CreateProductViewModel(productRepository, shelfRepository, dispatcher)
        advanceUntilIdle()

        viewModel.state.test {
            assertEquals("Shelf1", awaitItem().shelves.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onProductNameChange should update product name`() = scope.runTest {
        viewModel.state.test {
            skipItems(1)
            viewModel.onProductNameChange("Phone")
            assertEquals("Phone", awaitItem().productName)
        }
    }

    @Test
    fun `onPriceChange should update price`() = scope.runTest {
        viewModel.state.test {
            skipItems(1)
            viewModel.onPriceChange("12.50")
            assertEquals("12.50", awaitItem().price)
        }
    }
    @Test
    fun `onPriceAfterDiscountChange should update price after discount`() = scope.runTest {
        viewModel.state.test {
            skipItems(1)
            viewModel.onPriceAfterDiscountChange("10.50")
            assertEquals("10.50", awaitItem().priceAfterDiscount)
        }
    }

    @Test
    fun `onDescriptionChange should update description`() = scope.runTest {
        viewModel.state.test {
            skipItems(1)
            viewModel.onDescriptionChange("desc")
            assertEquals("desc", awaitItem().description)
        }
    }

    @Test
    fun `onShelfSelect should update shelf selected`() = scope.runTest {
        val shelf = CreateProductUiState.ShelfUiState("Shelf1")
        viewModel.updateState { copy(shelves = listOf(shelf)) }

        viewModel.state.test {
            skipItems(1)
            viewModel.onShelfSelect(shelf)
            val selected = awaitItem().shelves.first()
            assertTrue(selected.isSelected)
        }
    }

    @Test
    fun `onBackButton emits NavigateBack`() = scope.runTest {
        viewModel.effect.test {
            viewModel.onBackClicked()
            assertEquals(CreateProductEffect.NavigateBack, awaitItem())
        }
    }


    @Test
    fun `onDismissSnackBar hides snackbar`() = scope.runTest {
        viewModel.updateState { copy(showSnackBar = true) }

        viewModel.onDismissSnackBar()

        val state = viewModel.state.value
        assertFalse(state.showSnackBar)
        assertNull(state.snackBarUiState)
    }

    @Test
    fun `onUploadImageClick - too many images shows max limit error`() = scope.runTest {
        val fakeBitmap = mock<ImageBitmap>()
        every { fakeBitmap.width } returns 1
        every { fakeBitmap.height } returns 1

        viewModel.updateState {
            copy(images = List(CreateProductViewModel.IMAGE_MAX_LIMIT) {
                CreateProductUiState.ProductImageUi(
                    image = fakeBitmap,
                    imageSizeInMegaByte = 1.0,
                    imageState = ProductImageState.SUCCESS
                )
            })
        }

        val fakeFile = mock<ImageFile>()
        everySuspend { fakeFile.size() } returns 1
        everySuspend { fakeFile.toImageBitmap() } returns fakeBitmap
        everySuspend { fakeFile.toImageSrc() } returns mock<ImageSrc>()

        viewModel.onUploadImageClicked(fakeFile)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.showSnackBar)
        assertEquals(
            Res.string.error_image_max_limit.key,
            state.snackBarUiState?.message?.key
        )
    }

    @Test
    fun `onUploadImageClick - large file shows size error`() = scope.runTest {
        val fakeFile = mock<ImageFile>()
        val fakeBitmap = mock<ImageBitmap>()
        every { fakeBitmap.width } returns 100
        every { fakeBitmap.height } returns 100

        everySuspend { fakeFile.size() } returns (6 * 1024 * 1024L) // 6 MB
        everySuspend { fakeFile.toImageBitmap() } returns fakeBitmap
        everySuspend { fakeFile.toImageSrc() } returns mock<ImageSrc>()

        viewModel.onUploadImageClicked(fakeFile)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.showSnackBar)
        assertEquals(Res.string.error_image_size, state.snackBarUiState?.message)
    }

    @Test
    fun `onUploadImageClick - aspect ratio == 1 adds image immediately`() = scope.runTest {
        val fakeFile = mock<ImageFile>()
        val fakeBitmap = mock<ImageBitmap>()
        every { fakeBitmap.width } returns 100
        every { fakeBitmap.height } returns 100 // 1:1 ratio

        everySuspend { fakeFile.size() } returns (1024 * 1024L) // 1 MB
        everySuspend { fakeFile.toImageBitmap() } returns fakeBitmap
        everySuspend { fakeFile.toImageSrc() } returns mock<ImageSrc>()

        viewModel.onUploadImageClicked(fakeFile)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1, state.images.size)
        assertEquals(ProductImageState.SUCCESS, state.images.first().imageState)
    }

    @Test
    fun `onUploadImageClick - valid image goes to crop step`() = scope.runTest {
        val fakeFile = mock<ImageFile>()
        val fakeBitmap = mock<ImageBitmap>()
        every { fakeBitmap.width } returns 100
        every { fakeBitmap.height } returns 200 // ratio != 1

        everySuspend { fakeFile.size() } returns (1024 * 1024L)
        everySuspend { fakeFile.toImageBitmap() } returns fakeBitmap
        everySuspend { fakeFile.toImageSrc() } returns mock<ImageSrc>()

        viewModel.onUploadImageClicked(fakeFile)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.showCropImage)
        assertTrue(state.selectedImage != null)
    }

    @Test
    fun `onUploadImageClick - null imageSrc shows upload failed`() = scope.runTest {
        val fakeFile = mock<ImageFile>()
        val fakeBitmap = mock<ImageBitmap>()
        every { fakeBitmap.width } returns 100
        every { fakeBitmap.height } returns 200

        everySuspend { fakeFile.size() } returns (1024 * 1024L)
        everySuspend { fakeFile.toImageBitmap() } returns fakeBitmap
        everySuspend { fakeFile.toImageSrc() } returns null

        viewModel.onUploadImageClicked(fakeFile)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.showSnackBar)
        assertEquals(Res.string.error_upload_failed, state.snackBarUiState?.message)
    }

    @Test
    fun `onCancelImageClick should remove the given image`() = scope.runTest {
        val fakeBitmap1 = mock<ImageBitmap>()
        val fakeBitmap2 = mock<ImageBitmap>()

        viewModel.updateState {
            copy(
                images = listOf(
                    CreateProductUiState.ProductImageUi(
                        image = fakeBitmap1,
                        imageSizeInMegaByte = 1.0,
                        imageState = ProductImageState.SUCCESS
                    ),
                    CreateProductUiState.ProductImageUi(
                        image = fakeBitmap2,
                        imageSizeInMegaByte = 1.5,
                        imageState = ProductImageState.SUCCESS
                    )
                )
            )
        }

        viewModel.onCancelImageClicked(fakeBitmap1)

        val state = viewModel.state.value
        assertEquals(1, state.images.size)
        assertEquals(fakeBitmap2, state.images.first().image)
    }


    @Test
    fun `onCropImageBackClick should clear selected image and hide crop UI`() = scope.runTest {
        viewModel.updateState {
            copy(
                selectedImage = mock<ImageSrc>(), // anything non-null
                showCropImage = true
            )
        }

        viewModel.onCropImageBackClicked()

        val state = viewModel.state.value
        assertNull(state.selectedImage)
        assertFalse(state.showCropImage)
    }

    @Test
    fun `onAddProductClick - invalid product shows validation error`() = scope.runTest {
        viewModel.updateState {
            copy(
                productName = "Test",
                selectedShelf = CreateProductUiState.ShelfUiState("id1"),
                price = "abc",
                description = "Valid description".padEnd(120, 'x'),
                images = listOf(
                    CreateProductUiState.ProductImageUi(
                        id = 1234,
                        image = mock<ImageBitmap>(),
                        imageSizeInMegaByte = 1.0,
                        imageState = ProductImageState.SUCCESS
                    )
                )
            )
        }

        viewModel.onAddProductClicked()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.showSnackBar)
        assertEquals(Res.string.error_price_invalid, state.snackBarUiState?.message)
    }

    @Test
    fun `onAddProductClick - repository error shows error snackbar`() = scope.runTest {
        val fakeBitmap = mock<ImageBitmap>()
        val shelf = CreateProductUiState.ShelfUiState("s1", name = "Shelf1")

        everySuspend { productRepository.createProduct(any()) } throws RuntimeException("fail")

        viewModel.updateState {
            copy(
                productName = "Phone",
                selectedShelf = shelf,
                price = "50.0",
                description = "Nice description".padEnd(120, 'z'),
                images = listOf(
                    CreateProductUiState.ProductImageUi(
                        id = 0,
                        image = fakeBitmap,
                        imageSizeInMegaByte = 1.0,
                        imageState = ProductImageState.SUCCESS
                    )
                )
            )
        }

        viewModel.onAddProductClicked()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.showSnackBar)
        assertEquals(Res.string.error_general, state.snackBarUiState?.message)
    }
}

// ===== FAKE DATA FUNCTIONS =====

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
        shelfId = Uuid.parse("123e4567-e89b-12d3-a456-000000000123"),
        imageUrls = emptyList(),
        createdAt = "2025-10-10T12:00:00Z",
        quantityInCart = 2,
        isFavorite = false
    )
)

