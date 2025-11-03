package net.thechance.mena.dukan.presentation.viewModel.editProduct

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.attafitamim.krop.core.images.ImageSrc
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.delete_product_description
import mena.dukan_presentation.generated.resources.delete_product_success
import mena.dukan_presentation.generated.resources.delete_product_title
import mena.dukan_presentation.generated.resources.error_delete_product
import mena.dukan_presentation.generated.resources.error_general
import mena.dukan_presentation.generated.resources.error_image_max_limit
import mena.dukan_presentation.generated.resources.error_image_size
import mena.dukan_presentation.generated.resources.error_price_invalid
import mena.dukan_presentation.generated.resources.error_product_not_found
import mena.dukan_presentation.generated.resources.error_unauthorized_access
import mena.dukan_presentation.generated.resources.error_upload_failed
import mena.dukan_presentation.generated.resources.no_internet_connection
import mena.dukan_presentation.generated.resources.product_name_is_already_exist
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.exceptions.DuplicateNameException
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.exceptions.NoSuchItemException
import net.thechance.mena.dukan.domain.exceptions.UnAuthorizedException
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.presentation.component.product.productImage.ProductImageState
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.util.file.ImageFile
import net.thechance.mena.dukan.presentation.viewModel.createProduct.CreateProductUiState
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class EditProductViewModelTest {

    private val productRepository = mock<ProductRepository>(mode = MockMode.autofill)
    private val shelfRepository = mock<ShelfRepository>(mode = MockMode.autofill)
    private lateinit var viewModel: EditProductViewModel
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)
    private val productId = "test-product-id"

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        everySuspend { shelfRepository.getMyDukanShelves() } returns fakeShelves()
        everySuspend { productRepository.getProductById(any()) } returns fakeProduct()
        everySuspend { productRepository.updateProduct(any(), any()) } returns Unit
        everySuspend {
            productRepository.uploadProductImages(
                any(),
                any(),
                any()
            )
        } returns listOf("image-url")
        everySuspend { productRepository.deleteProductImages(any(), any()) } returns Unit
        everySuspend { productRepository.deleteProduct(any()) } returns Unit

        val savedStateHandle = SavedStateHandle(
            mapOf("productId" to productId)
        )

        viewModel = EditProductViewModel(
            productRepository = productRepository,
            shelfRepository = shelfRepository,
            savedStateHandle = savedStateHandle,
            dispatcher = dispatcher
        )
        scope.advanceUntilIdle()
    }

    @Test
    fun `init SHOULD load product data`() = scope.runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(fakeProduct().name, state.productName)
            assertEquals(fakeProduct().price.toString(), state.price)
            assertEquals(fakeProduct().description, state.description)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init SHOULD load shelves`() = scope.runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(fakeShelves().size, state.shelves.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init SHOULD set existing image URLs`() = scope.runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(fakeProduct().imageUrls, state.existingImageUrls)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onProductNameChange SHOULD update product name`() = scope.runTest {
        viewModel.onProductNameChange("New Product Name")
        assertEquals("New Product Name", viewModel.state.value.productName)
    }

    @Test
    fun `onPriceChange SHOULD update price`() = scope.runTest {
        viewModel.onPriceChange("99.99")
        assertEquals("99.99", viewModel.state.value.price)
    }

    @Test
    fun `onPriceChange SHOULD filter non-digit characters`() = scope.runTest {
        viewModel.onPriceChange("99.99abc")
        assertEquals("99.99", viewModel.state.value.price)
    }

    @Test
    fun `onDescriptionChange SHOULD update description`() = scope.runTest {
        viewModel.onDescriptionChange("New description")
        assertEquals("New description", viewModel.state.value.description)
    }

    @Test
    fun `onShelfSelect SHOULD update selected shelf`() = scope.runTest {
        val shelf = CreateProductUiState.ShelfUiState(
            id = fakeShelves()[0].id.toString(),
            name = fakeShelves()[0].name,
            isSelected = true
        )
        viewModel.onShelfSelect(shelf)
        val state = viewModel.state.value
        assertEquals(shelf.id, state.selectedShelf?.id)
        assertTrue(state.shelves.first { it.id == shelf.id }.isSelected)
    }

    @Test
    fun `onBackClicked SHOULD emit NavigateBack`() = scope.runTest {
        viewModel.effect.test {
            viewModel.onBackClicked()
            assertEquals(EditProductEffect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `onDeleteProductClicked SHOULD show delete dialog`() = scope.runTest {
        viewModel.onDeleteProductClicked()
        val state = viewModel.state.value
        assertNotNull(state.deleteDialog)
        assertEquals(Res.string.delete_product_title.key, state.deleteDialog?.title?.key)
        assertEquals(
            Res.string.delete_product_description.key,
            state.deleteDialog?.description?.key
        )
    }

    @Test
    fun `onDismissDeleteDialog SHOULD hide delete dialog`() = scope.runTest {
        viewModel.onDeleteProductClicked()
        assertNotNull(viewModel.state.value.deleteDialog)

        viewModel.onDismissDeleteDialog()
        assertNull(viewModel.state.value.deleteDialog)
    }

    @Test
    fun `onDeleteConfirmed SHOULD delete product and navigate on success`() = scope.runTest {
        viewModel.onDeleteProductClicked()

        viewModel.effect.test {
            viewModel.onDeleteConfirmed()
            advanceUntilIdle()

            val state = viewModel.state.value
            assertTrue(state.showSnackBar)
            assertEquals(
                Res.string.delete_product_success.key,
                state.snackBarUiState?.message?.key
            )

            assertEquals(EditProductEffect.NavigateToManageDukanProducts, awaitItem())
        }
    }

    @Test
    fun `onDeleteConfirmed - no internet shows error`() = scope.runTest {
        everySuspend { productRepository.deleteProduct(any()) } throws NoInternetException()

        viewModel.onDeleteProductClicked()
        viewModel.onDeleteConfirmed()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.showSnackBar)
        assertEquals(
            Res.string.no_internet_connection.key,
            state.snackBarUiState?.message?.key
        )
        assertNull(state.deleteDialog)
    }

    @Test
    fun `onDeleteConfirmed - product not found shows error`() = scope.runTest {
        everySuspend { productRepository.deleteProduct(any()) } throws NoSuchItemException()

        viewModel.onDeleteProductClicked()
        viewModel.onDeleteConfirmed()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.showSnackBar)
        assertEquals(
            Res.string.error_product_not_found.key,
            state.snackBarUiState?.message?.key
        )
        assertNull(state.deleteDialog)
    }

    @Test
    fun `onDeleteConfirmed - unauthorized shows error`() = scope.runTest {
        everySuspend { productRepository.deleteProduct(any()) } throws UnAuthorizedException()

        viewModel.onDeleteProductClicked()
        viewModel.onDeleteConfirmed()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.showSnackBar)
        assertEquals(
            Res.string.error_unauthorized_access.key,
            state.snackBarUiState?.message?.key
        )
        assertNull(state.deleteDialog)
    }

    @Test
    fun `onDeleteConfirmed - general error shows error message`() = scope.runTest {
        everySuspend { productRepository.deleteProduct(any()) } throws RuntimeException("Unknown error")

        viewModel.onDeleteProductClicked()
        viewModel.onDeleteConfirmed()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.showSnackBar)
        assertEquals(
            Res.string.error_delete_product.key,
            state.snackBarUiState?.message?.key
        )
        assertNull(state.deleteDialog)
    }

    @Test
    fun `onDismissSnackBar SHOULD hide snackbar`() = scope.runTest {
        viewModel.updateState {
            copy(
                showSnackBar = true,
                snackBarUiState = SnackBarUiState(
                    message = Res.string.error_general,
                    snackBarType = SnackBarType.ERROR
                )
            )
        }

        viewModel.onDismissSnackBar()

        val state = viewModel.state.value
        assertFalse(state.showSnackBar)
        assertNull(state.snackBarUiState)
    }

    @Test
    fun `onCancelExistingImageUrl SHOULD remove image URL`() = scope.runTest {
        val initialUrls = viewModel.state.value.existingImageUrls
        assertTrue(initialUrls.isNotEmpty())

        viewModel.onCancelExistingImageUrl(initialUrls.first())
        val state = viewModel.state.value
        assertFalse(state.existingImageUrls.contains(initialUrls.first()))
        assertEquals(initialUrls.size - 1, state.existingImageUrls.size)
    }

    @Test
    fun `onCancelImageClicked SHOULD remove image`() = scope.runTest {
        val fakeBitmap1 = mock<ImageBitmap>()
        val fakeBitmap2 = mock<ImageBitmap>()

        viewModel.updateState {
            copy(
                images = listOf(
                    EditProductUiState.ProductImageUi(
                        image = fakeBitmap1,
                        imageSizeInMegaByte = 1.0,
                        imageState = ProductImageState.SUCCESS
                    ),
                    EditProductUiState.ProductImageUi(
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
    fun `onCropImageBackClicked SHOULD clear selected image and hide crop UI`() = scope.runTest {
        viewModel.updateState {
            copy(
                selectedImage = mock<ImageSrc>(),
                showCropImage = true
            )
        }

        viewModel.onCropImageBackClicked()

        val state = viewModel.state.value
        assertNull(state.selectedImage)
        assertFalse(state.showCropImage)
    }

    @Test
    fun `onUploadImageClicked - too many images shows max limit error`() = scope.runTest {
        val fakeBitmap = mock<ImageBitmap>()
        every { fakeBitmap.width } returns 1
        every { fakeBitmap.height } returns 1

        viewModel.updateState {
            copy(
                images = List(EditProductViewModel.IMAGE_MAX_LIMIT - 1) {
                    EditProductUiState.ProductImageUi(
                        image = fakeBitmap,
                        imageSizeInMegaByte = 1.0,
                        imageState = ProductImageState.SUCCESS
                    )
                },
                existingImageUrls = listOf("existing-url")
            )
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
            Res.string.error_image_max_limit,
            state.snackBarUiState?.message
        )
    }

    @Test
    fun `onUploadImageClicked - large file shows size error`() = scope.runTest {
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
        assertEquals(
            Res.string.error_image_size.key,
            state.snackBarUiState?.message?.key
        )
    }

    @Test
    fun `onUploadImageClicked - aspect ratio == 1 adds image immediately`() = scope.runTest {
        val fakeFile = mock<ImageFile>()
        val fakeBitmap = mock<ImageBitmap>()
        every { fakeBitmap.width } returns 100
        every { fakeBitmap.height } returns 100

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
    fun `onUploadImageClicked - valid image goes to crop step`() = scope.runTest {
        val fakeFile = mock<ImageFile>()
        val fakeBitmap = mock<ImageBitmap>()
        every { fakeBitmap.width } returns 100
        every { fakeBitmap.height } returns 200

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
    fun `onUploadImageClicked - null imageSrc shows upload failed`() = scope.runTest {
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
        assertEquals(
            Res.string.error_upload_failed.key,
            state.snackBarUiState?.message?.key
        )
    }

    @Test
    fun `onSaveProductClicked - invalid product shows validation error`() = scope.runTest {
        viewModel.updateState {
            copy(
                productName = "Test",
                selectedShelf = EditProductUiState.ShelfUiState("id1", "Shelf1", true),
                price = "abc",
                description = "Valid description".padEnd(120, 'x'),
                images = listOf(
                    EditProductUiState.ProductImageUi(
                        id = 1234,
                        image = mock<ImageBitmap>(),
                        imageSizeInMegaByte = 1.0,
                        imageState = ProductImageState.SUCCESS
                    )
                )
            )
        }

        viewModel.onSaveProductClicked()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.showSnackBar)
        assertEquals(
            Res.string.error_price_invalid.key,
            state.snackBarUiState?.message?.key
        )
    }

    @Test
    fun `onSaveProductClicked - duplicate name shows error`() = scope.runTest {
        everySuspend {
            productRepository.updateProduct(
                any(),
                any()
            )
        } throws DuplicateNameException()
        everySuspend { productRepository.deleteProductImages(any(), any()) } returns Unit

        val firstShelf = fakeShelves().first()
        val selectedShelfUi = EditProductUiState.ShelfUiState(
            id = firstShelf.id.toString(),
            name = firstShelf.name,
            isSelected = true
        )
        viewModel.updateState {
            copy(
                productName = "Duplicate Name",
                selectedShelf = selectedShelfUi,
                shelves = shelves.map {
                    if (it.id == selectedShelfUi.id) selectedShelfUi else it
                },
                price = "50.0",
                description = "Nice description".padEnd(120, 'z'),
                existingImageUrls = fakeProduct().imageUrls,
                images = emptyList()
            )
        }

        viewModel.onSaveProductClicked()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.showSnackBar)
        assertEquals(
            Res.string.product_name_is_already_exist.key,
            state.snackBarUiState?.message?.key
        )
    }

    @Test
    fun `getProductData - error shows product not found`() = scope.runTest {
        everySuspend { productRepository.getProductById(any()) } throws NoSuchItemException()

        val savedStateHandle = SavedStateHandle(mapOf("productId" to productId))
        val errorViewModel = EditProductViewModel(
            productRepository = productRepository,
            shelfRepository = shelfRepository,
            savedStateHandle = savedStateHandle,
            dispatcher = dispatcher
        )
        advanceUntilIdle()

        errorViewModel.state.test {
            val state = awaitItem()
            assertTrue(state.showSnackBar)
            assertEquals(
                Res.string.error_product_not_found.key,
                state.snackBarUiState?.message?.key
            )
        }
    }

    @Test
    fun `getProductData - no internet shows error`() = scope.runTest {
        everySuspend { productRepository.getProductById(any()) } throws NoInternetException()

        val savedStateHandle = SavedStateHandle(mapOf("productId" to productId))
        val errorViewModel = EditProductViewModel(
            productRepository = productRepository,
            shelfRepository = shelfRepository,
            savedStateHandle = savedStateHandle,
            dispatcher = dispatcher
        )
        advanceUntilIdle()

        errorViewModel.state.test {
            val state = awaitItem()
            assertTrue(state.showSnackBar)
            assertEquals(
                Res.string.no_internet_connection.key,
                state.snackBarUiState?.message?.key
            )
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
private val testShelfId = Uuid.parse("123e4567-e89b-12d3-a456-426614174000")

@OptIn(ExperimentalUuidApi::class)
private fun fakeProduct(): Product {
    return Product(
        id = Uuid.parse("013e0bb1-6177-4430-ae08-f3a1a24f6f7d"),
        name = "Test Product",
        description = "Test Description".padEnd(120, 'x'),
        price = 99.99,
        shelfId = testShelfId,
        imageUrls = listOf("image1.jpg", "image2.jpg"),
        createdAt = "2025-09-16T15:06:57.507394",
    )
}

@OptIn(ExperimentalUuidApi::class)
private fun fakeShelves(): List<Shelf> {
    return listOf(
        Shelf(id = testShelfId, name = "Shelf 1"),
        Shelf(id = Uuid.parse("223e4567-e89b-12d3-a456-426614174001"), name = "Shelf 2"),
        Shelf(id = Uuid.parse("323e4567-e89b-12d3-a456-426614174002"), name = "Shelf 3")
    )
}

