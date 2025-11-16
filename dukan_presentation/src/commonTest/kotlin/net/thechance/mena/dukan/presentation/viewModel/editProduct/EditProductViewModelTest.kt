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
import kotlinx.coroutines.CoroutineDispatcher
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
import mena.dukan_presentation.generated.resources.error_description_length
import mena.dukan_presentation.generated.resources.error_general
import mena.dukan_presentation.generated.resources.error_image_max_limit
import mena.dukan_presentation.generated.resources.error_image_size
import mena.dukan_presentation.generated.resources.error_price_invalid
import mena.dukan_presentation.generated.resources.error_price_not_positive
import mena.dukan_presentation.generated.resources.error_product_not_found
import mena.dukan_presentation.generated.resources.error_unauthorized_access
import mena.dukan_presentation.generated.resources.error_update_product
import mena.dukan_presentation.generated.resources.error_upload_failed
import mena.dukan_presentation.generated.resources.invalid_image_format
import mena.dukan_presentation.generated.resources.no_internet_connection
import mena.dukan_presentation.generated.resources.product_name_is_already_exist
import mena.dukan_presentation.generated.resources.save_product_success
import net.thechance.mena.dukan.domain.entity.Price
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.entity.Shelf
import net.thechance.mena.dukan.domain.exceptions.CreationFailedException
import net.thechance.mena.dukan.domain.exceptions.DuplicateNameException
import net.thechance.mena.dukan.domain.exceptions.InvalidImageFormatException
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.exceptions.NoSuchItemException
import net.thechance.mena.dukan.domain.exceptions.UnAuthorizedException
import net.thechance.mena.dukan.domain.exceptions.UploadingFailedException
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
            productRepository.uploadProductImages(any(), any(), any())
        } returns listOf("image-url")
        everySuspend { productRepository.deleteProductImages(any(), any()) } returns Unit
        everySuspend { productRepository.deleteProduct(any()) } returns Unit

        viewModel = createEditProductViewModel(
            productRepository = productRepository,
            shelfRepository = shelfRepository,
            productId = productId,
            dispatcher = dispatcher
        )
        scope.advanceUntilIdle()
    }

    @Test
    fun `init SHOULD load product data`() = scope.runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(fakeProduct().name, state.productName)
            assertEquals(fakeProduct().price.base.toString(), state.price)
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
    fun `shelvesForShelfSection SHOULD map shelves to CreateProductUiState ShelfUiState`() =
        scope.runTest {
            val state = viewModel.state.value
            val shelvesForSection = state.shelvesForShelfSection

            assertEquals(state.shelves.size, shelvesForSection.size)

            state.shelves.forEachIndexed { index, shelf ->
                val mappedShelf = shelvesForSection[index]
                assertEquals(shelf.id, mappedShelf.id)
                assertEquals(shelf.name, mappedShelf.name)
                assertEquals(shelf.isSelected, mappedShelf.isSelected)
            }
        }

    @Test
    fun `shelvesForShelfSection SHOULD return empty list when shelves are empty`() = scope.runTest {
        viewModel.updateState { copy(shelves = emptyList()) }

        val state = viewModel.state.value
        val shelvesForSection = state.shelvesForShelfSection

        assertTrue(shelvesForSection.isEmpty())
    }

    @Test
    fun `shelvesForShelfSection SHOULD preserve selection state`() = scope.runTest {
        val selectedShelfUi = createSelectedShelfUi()

        viewModel.updateState {
            copy(
                shelves = shelves.map {
                    if (it.id == selectedShelfUi.id) selectedShelfUi else it
                }
            )
        }

        val state = viewModel.state.value
        val shelvesForSection = state.shelvesForShelfSection

        val mappedSelectedShelf = shelvesForSection.first { it.id == selectedShelfUi.id }
        assertTrue(mappedSelectedShelf.isSelected)

        val mappedUnselectedShelves = shelvesForSection.filter { it.id != selectedShelfUi.id }
        mappedUnselectedShelves.forEach { shelf ->
            assertFalse(shelf.isSelected)
        }
    }

    @Test
    fun `shelvesForShelfSection SHOULD map all shelf properties correctly`() = scope.runTest {
        val customShelves = listOf(
            EditProductUiState.ShelfUiState(id = "id1", name = "Shelf 1", isSelected = true),
            EditProductUiState.ShelfUiState(id = "id2", name = "Shelf 2", isSelected = false),
            EditProductUiState.ShelfUiState(id = "id3", name = "Shelf 3", isSelected = false)
        )

        viewModel.updateState { copy(shelves = customShelves) }

        val state = viewModel.state.value
        val shelvesForSection = state.shelvesForShelfSection

        assertEquals(3, shelvesForSection.size)

        customShelves.forEachIndexed { index, shelf ->
            val mappedShelf = shelvesForSection[index]
            assertEquals(shelf.id, mappedShelf.id, "Shelf $index id should match")
            assertEquals(shelf.name, mappedShelf.name, "Shelf $index name should match")
            assertEquals(
                shelf.isSelected,
                mappedShelf.isSelected,
                "Shelf $index selection state should match"
            )
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
    fun `onPriceAfterDiscountChange SHOULD update price after discount`() = scope.runTest {
        viewModel.onPriceAfterDiscountChange("80.99")
        assertEquals("80.99", viewModel.state.value.priceAfterDiscount)
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
    fun `onOutOfStockChange SHOULD update isOutOfStock to true`() = scope.runTest {
        viewModel.onOutOfStockChange(true)
        assertTrue(viewModel.state.value.isOutOfStock)
    }

    @Test
    fun `onOutOfStockChange SHOULD update isOutOfStock to false`() = scope.runTest {
        viewModel.updateState { copy(isOutOfStock = true) }
        assertTrue(viewModel.state.value.isOutOfStock)

        viewModel.onOutOfStockChange(false)
        assertFalse(viewModel.state.value.isOutOfStock)
    }

    @Test
    fun `onOutOfStockChange SHOULD call updateButtonState`() = scope.runTest {
        viewModel.updateStateWithValidProduct()

        viewModel.onOutOfStockChange(true)
        val updatedState = viewModel.state.value

        assertTrue(updatedState.isOutOfStock)
        assertNotNull(updatedState.isSaveButtonEnabled)
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
        assertEquals(Res.string.delete_product_title.key, state.deleteDialog.title.key)
        assertEquals(
            Res.string.delete_product_description.key,
            state.deleteDialog.description.key
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

        everySuspend { fakeFile.size() } returns (6 * 1024 * 1024L)
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

        everySuspend { fakeFile.size() } returns (1024 * 1024L)
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
        viewModel.updateStateWithValidProduct(
            productName = "Test",
            price = "abc",
            selectedShelf = createSimpleShelfUi(),
            images = listOf(createValidProductImageUi())
        )

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
            productRepository.updateProduct(any(), any())
        } throws DuplicateNameException()
        everySuspend { productRepository.deleteProductImages(any(), any()) } returns Unit

        viewModel.updateStateWithValidProduct(
            productName = "Duplicate Name",
            description = "Nice description".padEnd(120, 'z'),
            existingImageUrls = fakeProduct().imageUrls
        )

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

        val errorViewModel = createEditProductViewModel(
            productRepository = productRepository,
            shelfRepository = shelfRepository,
            productId = productId,
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

        val errorViewModel = createEditProductViewModel(
            productRepository = productRepository,
            shelfRepository = shelfRepository,
            productId = productId,
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

    @Test
    fun `getProductData - general error shows error`() = scope.runTest {
        everySuspend { productRepository.getProductById(any()) } throws RuntimeException("General error")

        val errorViewModel = createEditProductViewModel(
            productRepository = productRepository,
            shelfRepository = shelfRepository,
            productId = productId,
            dispatcher = dispatcher
        )
        advanceUntilIdle()

        errorViewModel.state.test {
            val state = awaitItem()
            assertTrue(state.showSnackBar)
            assertEquals(
                Res.string.error_general.key,
                state.snackBarUiState?.message?.key
            )
        }
    }

    @Test
    fun `filterValidImageUrls SHOULD filter out empty and blank URLs`() = scope.runTest {
        setupProductRepositoryWithCustomProduct(
            productRepository,
            fakeProduct().copy(imageUrls = listOf("valid1.jpg", "", "   ", "valid2.jpg"))
        )

        val testViewModel = createEditProductViewModel(
            productRepository = productRepository,
            shelfRepository = shelfRepository,
            productId = productId,
            dispatcher = dispatcher
        )
        advanceUntilIdle()

        val state = testViewModel.state.value
        assertEquals(2, state.existingImageUrls.size)
        assertTrue(state.existingImageUrls.contains("valid1.jpg"))
        assertTrue(state.existingImageUrls.contains("valid2.jpg"))
    }

    @Test
    fun `onGetShelvesSuccess - when productShelfId doesn't match any shelf SHOULD not select shelf`() =
        scope.runTest {
            setupProductRepositoryWithCustomProduct(
                productRepository,
                fakeProduct().copy(shelfId = Uuid.parse("99999999-9999-9999-9999-999999999999"))
            )

            val testViewModel = createEditProductViewModel(
                productRepository = productRepository,
                shelfRepository = shelfRepository,
                productId = productId,
                dispatcher = dispatcher
            )
            advanceUntilIdle()

            val state = testViewModel.state.value
            assertNotNull(state.shelves)
            assertTrue(state.shelves.isNotEmpty())
            assertNull(state.selectedShelf)
            assertFalse(state.shelves.any { it.isSelected })
        }

    @Test
    fun `onGetShelvesSuccess - when shelves arrive after product data SHOULD select shelf`() =
        scope.runTest {
            val state = viewModel.state.value
            assertNotNull(state.selectedShelf)
            assertEquals(testShelfId.toString(), state.selectedShelf.id)
        }

    @Test
    fun `selectProductShelf - when shelves are empty SHOULD not crash`() = scope.runTest {
        viewModel.updateState { copy(shelves = emptyList(), selectedShelf = null) }

        val state = viewModel.state.value
        assertTrue(state.shelves.isEmpty())
        assertNull(state.selectedShelf)
    }

    @Test
    fun `onErrorGettingShelves - no internet shows error`() = scope.runTest {
        everySuspend { shelfRepository.getMyDukanShelves() } throws NoInternetException()

        val errorViewModel = createEditProductViewModel(
            productRepository = productRepository,
            shelfRepository = shelfRepository,
            productId = productId,
            dispatcher = dispatcher
        )
        advanceUntilIdle()

        val state = errorViewModel.state.value
        assertTrue(state.showSnackBar)
        assertFalse(state.isShelvesLoading)
        assertEquals(
            Res.string.no_internet_connection.key,
            state.snackBarUiState?.message?.key
        )
    }

    @Test
    fun `onErrorGettingShelves - general error shows error`() = scope.runTest {
        everySuspend { shelfRepository.getMyDukanShelves() } throws RuntimeException("General error")

        val errorViewModel = createEditProductViewModel(
            productRepository = productRepository,
            shelfRepository = shelfRepository,
            productId = productId,
            dispatcher = dispatcher
        )
        advanceUntilIdle()

        val state = errorViewModel.state.value
        assertTrue(state.showSnackBar)
        assertFalse(state.isShelvesLoading)
        assertEquals(
            Res.string.error_general.key,
            state.snackBarUiState?.message?.key
        )
    }

    @Test
    fun `onSaveProductClicked - successful save navigates and shows success`() = scope.runTest {
        viewModel.updateStateWithValidProduct(
            productName = "Updated Product",
            description = "Updated description".padEnd(120, 'x')
        )

        viewModel.effect.test {
            viewModel.onSaveProductClicked()
            advanceUntilIdle()

            val state = viewModel.state.value
            assertFalse(state.isSaveButtonLoading)
            assertTrue(state.showSnackBar)
            assertEquals(
                Res.string.save_product_success.key,
                state.snackBarUiState?.message?.key
            )

            assertEquals(EditProductEffect.NavigateToManageDukanProducts, awaitItem())
        }
    }

    @Test
    fun `onSaveProductClicked - price not positive shows error`() = scope.runTest {
        viewModel.updateStateWithValidProduct(
            productName = "Test",
            price = "0.0",
            selectedShelf = createSimpleShelfUi(),
            images = listOf(createValidProductImageUi())
        )

        viewModel.onSaveProductClicked()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.showSnackBar)
        assertEquals(
            Res.string.error_price_not_positive.key,
            state.snackBarUiState?.message?.key
        )
    }

    @Test
    fun `onSaveProductClicked - description too short shows error`() = scope.runTest {
        viewModel.updateStateWithValidProduct(
            productName = "Test",
            price = "50.0",
            description = "Short",
            selectedShelf = createSimpleShelfUi(),
            images = listOf(createValidProductImageUi())
        )

        viewModel.onSaveProductClicked()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.showSnackBar)
        assertEquals(
            Res.string.error_description_length.key,
            state.snackBarUiState?.message?.key
        )
    }

    @Test
    fun `onSaveProductClicked - description too long shows error`() = scope.runTest {
        viewModel.updateStateWithValidProduct(
            productName = "Test",
            price = "50.0",
            description = "x".repeat(3001),
            selectedShelf = createSimpleShelfUi(),
            images = listOf(createValidProductImageUi())
        )

        viewModel.onSaveProductClicked()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.showSnackBar)
        assertEquals(
            Res.string.error_description_length.key,
            state.snackBarUiState?.message?.key
        )
    }

    @Test
    fun `onSaveProductClicked - empty final image URLs shows error`() = scope.runTest {
        viewModel.updateStateWithValidProduct(
            productName = "Test Product",
            existingImageUrls = emptyList(),
            images = emptyList()
        )

        viewModel.onSaveProductClicked()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.showSnackBar)
        assertEquals(
            Res.string.error_general.key,
            state.snackBarUiState?.message?.key
        )
        assertFalse(state.isSaveButtonLoading)
    }

    @Test
    fun `resetUiStateAfterValidationFailure SHOULD reset UI state correctly`() = scope.runTest {
        val fakeBitmap = mock<ImageBitmap>()
        every { fakeBitmap.width } returns 100
        every { fakeBitmap.height } returns 100

        everySuspend {
            productRepository.uploadProductImages(any(), any(), any())
        } returns emptyList()

        viewModel.updateStateWithValidProduct(
            productName = "Test Product",
            existingImageUrls = emptyList(),
            images = listOf(createValidProductImageUi(image = fakeBitmap))
        )

        var state = viewModel.state.value
        assertTrue(state.isUploadingImageEnabled)
        assertTrue(state.isTextFieldEnabled)
        assertTrue(state.isCancelImageEnabled)
        assertFalse(state.isSaveButtonLoading)

        viewModel.onSaveProductClicked()
        advanceUntilIdle()

        state = viewModel.state.value
        assertFalse(state.isSaveButtonLoading, "Save button loading should be false")
        assertTrue(state.isUploadingImageEnabled, "Uploading image should be enabled")
        assertTrue(state.isTextFieldEnabled, "Text field should be enabled")
        assertTrue(state.isCancelImageEnabled, "Cancel image should be enabled")
        assertTrue(state.showSnackBar, "Should show snackbar with error")
        assertEquals(
            Res.string.error_general.key,
            state.snackBarUiState?.message?.key
        )
    }

    @Test
    fun `onSaveProductClicked - InvalidImageFormatException shows error`() = scope.runTest {
        everySuspend {
            productRepository.updateProduct(any(), any())
        } throws InvalidImageFormatException()

        viewModel.updateStateWithValidProduct(productName = "Test Product")

        viewModel.onSaveProductClicked()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.showSnackBar)
        assertEquals(
            Res.string.invalid_image_format.key,
            state.snackBarUiState?.message?.key
        )
    }

    @Test
    fun `onSaveProductClicked - CreationFailedException shows error`() = scope.runTest {
        everySuspend {
            productRepository.updateProduct(any(), any())
        } throws CreationFailedException()

        viewModel.updateStateWithValidProduct(productName = "Test Product")

        viewModel.onSaveProductClicked()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.showSnackBar)
        assertEquals(
            Res.string.error_update_product.key,
            state.snackBarUiState?.message?.key
        )
    }

    @Test
    fun `onUploadImageClicked - UploadingFailedException in onErrorUploadingImages shows error`() =
        scope.runTest {
            val fakeFile = mock<ImageFile>()
            val fakeBitmap = mock<ImageBitmap>()
            every { fakeBitmap.width } returns 100
            every { fakeBitmap.height } returns 100

            everySuspend { fakeFile.size() } returns (1024 * 1024L)
            everySuspend { fakeFile.toImageBitmap() } returns fakeBitmap
            everySuspend { fakeFile.toImageSrc() } throws UploadingFailedException()

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
    fun `onUploadImageClicked - InvalidImageFormatException in onErrorUploadingImages shows error`() =
        scope.runTest {
            val fakeFile = mock<ImageFile>()
            val fakeBitmap = mock<ImageBitmap>()
            every { fakeBitmap.width } returns 100
            every { fakeBitmap.height } returns 100

            everySuspend { fakeFile.size() } returns (1024 * 1024L)
            everySuspend { fakeFile.toImageBitmap() } returns fakeBitmap
            everySuspend { fakeFile.toImageSrc() } throws InvalidImageFormatException()

            viewModel.onUploadImageClicked(fakeFile)
            advanceUntilIdle()

            val state = viewModel.state.value
            assertTrue(state.showSnackBar)
            assertEquals(
                Res.string.invalid_image_format.key,
                state.snackBarUiState?.message?.key
            )
        }

    @Test
    fun `onUploadImageClicked - NoInternetException in onErrorUploadingImages shows error`() =
        scope.runTest {
            val fakeFile = mock<ImageFile>()
            val fakeBitmap = mock<ImageBitmap>()
            every { fakeBitmap.width } returns 100
            every { fakeBitmap.height } returns 100

            everySuspend { fakeFile.size() } returns (1024 * 1024L)
            everySuspend { fakeFile.toImageBitmap() } returns fakeBitmap
            everySuspend { fakeFile.toImageSrc() } throws NoInternetException()

            viewModel.onUploadImageClicked(fakeFile)
            advanceUntilIdle()

            val state = viewModel.state.value
            assertTrue(state.showSnackBar)
            assertEquals(
                Res.string.no_internet_connection.key,
                state.snackBarUiState?.message?.key
            )
        }

    @Test
    fun `onSaveProductClicked - isProductValid returns false when name is empty`() = scope.runTest {
        viewModel.updateStateWithValidProduct(
            productName = "   ",
            selectedShelf = createSimpleShelfUi(),
            images = listOf(createValidProductImageUi())
        )

        viewModel.onSaveProductClicked()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.showSnackBar)
        assertEquals(
            Res.string.error_general.key,
            state.snackBarUiState?.message?.key
        )
    }

    @Test
    fun `onSaveProductClicked - isProductValid returns false when no images`() = scope.runTest {
        viewModel.updateStateWithValidProduct(
            productName = "Test",
            selectedShelf = createSimpleShelfUi(),
            existingImageUrls = emptyList(),
            images = emptyList()
        )

        viewModel.onSaveProductClicked()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.showSnackBar)
        assertEquals(
            Res.string.error_general.key,
            state.snackBarUiState?.message?.key
        )
    }

    @Test
    fun `getProductData - product with empty imageUrls SHOULD load successfully`() = scope.runTest {
        setupProductRepositoryWithCustomProduct(
            productRepository,
            fakeProduct().copy(imageUrls = emptyList())
        )

        val testViewModel = createEditProductViewModel(
            productRepository = productRepository,
            shelfRepository = shelfRepository,
            productId = productId,
            dispatcher = dispatcher
        )
        advanceUntilIdle()

        val state = testViewModel.state.value
        assertEquals(fakeProduct().name, state.productName)
        assertEquals("99.99", state.price)
        assertEquals("99.99", state.priceAfterDiscount)
        assertTrue(state.existingImageUrls.isEmpty())
    }

    @Test
    fun `getProductData - product with only empty imageUrls SHOULD filter them out`() =
        scope.runTest {
            setupProductRepositoryWithCustomProduct(
                productRepository,
                fakeProduct().copy(imageUrls = listOf("", "   ", "\t\n"))
            )

            val testViewModel = createEditProductViewModel(
                productRepository = productRepository,
                shelfRepository = shelfRepository,
                productId = productId,
                dispatcher = dispatcher
            )
            advanceUntilIdle()

            val state = testViewModel.state.value
            assertTrue(state.existingImageUrls.isEmpty())
        }

    @Test
    fun `getProductData - product with all valid imageUrls SHOULD load all URLs`() = scope.runTest {
        val imageUrls = listOf("url1.jpg", "url2.jpg", "url3.jpg", "url4.jpg", "url5.jpg")
        setupProductRepositoryWithCustomProduct(
            productRepository,
            fakeProduct().copy(imageUrls = imageUrls)
        )

        val testViewModel = createEditProductViewModel(
            productRepository = productRepository,
            shelfRepository = shelfRepository,
            productId = productId,
            dispatcher = dispatcher
        )
        advanceUntilIdle()

        val state = testViewModel.state.value
        assertEquals(imageUrls.size, state.existingImageUrls.size)
        assertEquals(imageUrls, state.existingImageUrls)
    }

    @Test
    fun `getProductData - product with different shelfId SHOULD not select shelf if not in list`() =
        scope.runTest {
            val differentShelfId = Uuid.parse("99999999-9999-9999-9999-999999999999")
            setupProductRepositoryWithCustomProduct(
                productRepository,
                fakeProduct().copy(shelfId = differentShelfId)
            )

            val testViewModel = createEditProductViewModel(
                productRepository = productRepository,
                shelfRepository = shelfRepository,
                productId = productId,
                dispatcher = dispatcher
            )
            advanceUntilIdle()

            val state = testViewModel.state.value
            assertNull(state.selectedShelf)
            assertFalse(state.shelves.any { it.isSelected })
        }

    @Test
    fun `getProductData - product with zero price SHOULD load successfully`() = scope.runTest {
        setupProductRepositoryWithCustomProduct(
            productRepository,
            fakeProduct().copy(price = Price(base = 0.0, final = 0.0))
        )

        val testViewModel = createEditProductViewModel(
            productRepository = productRepository,
            shelfRepository = shelfRepository,
            productId = productId,
            dispatcher = dispatcher
        )
        advanceUntilIdle()

        val state = testViewModel.state.value
        assertEquals("0.0", state.price.trim())
    }

    @Test
    fun `getProductData - product with very large price SHOULD load successfully`() =
        scope.runTest {
            setupProductRepositoryWithCustomProduct(
                productRepository,
                fakeProduct().copy(price = Price(base = 999999.99))
            )

            val testViewModel = createEditProductViewModel(
                productRepository = productRepository,
                shelfRepository = shelfRepository,
                productId = productId,
                dispatcher = dispatcher
            )
            advanceUntilIdle()

            val state = testViewModel.state.value
            assertEquals("999999.99", state.price)
        }

    @Test
    fun `getProductData - product with minimum description length SHOULD load successfully`() =
        scope.runTest {
            val minDescription = "x".repeat(100)
            setupProductRepositoryWithCustomProduct(
                productRepository,
                fakeProduct().copy(description = minDescription)
            )

            val testViewModel = createEditProductViewModel(
                productRepository = productRepository,
                shelfRepository = shelfRepository,
                productId = productId,
                dispatcher = dispatcher
            )
            advanceUntilIdle()

            val state = testViewModel.state.value
            assertEquals(minDescription, state.description)
        }

    @Test
    fun `getProductData - product with maximum description length SHOULD load successfully`() =
        scope.runTest {
            val maxDescription = "x".repeat(3000)
            setupProductRepositoryWithCustomProduct(
                productRepository,
                fakeProduct().copy(description = maxDescription)
            )

            val testViewModel = createEditProductViewModel(
                productRepository = productRepository,
                shelfRepository = shelfRepository,
                productId = productId,
                dispatcher = dispatcher
            )
            advanceUntilIdle()

            val state = testViewModel.state.value
            assertEquals(maxDescription, state.description)
        }

    @Test
    fun `getProductData - product with different id SHOULD load successfully`() = scope.runTest {
        val differentId = Uuid.parse("99999999-9999-9999-9999-999999999998")
        setupProductRepositoryWithCustomProduct(
            productRepository,
            fakeProduct().copy(id = differentId)
        )

        val testViewModel = createEditProductViewModel(
            productRepository = productRepository,
            shelfRepository = shelfRepository,
            productId = productId,
            dispatcher = dispatcher
        )
        advanceUntilIdle()

        val state = testViewModel.state.value
        assertEquals(fakeProduct().copy(id = differentId).name, state.productName)
    }

    @Test
    fun `getProductData - product with different createdAt SHOULD load successfully`() =
        scope.runTest {
            val differentCreatedAt = "2024-01-01T00:00:00.000000"
            setupProductRepositoryWithCustomProduct(
                productRepository,
                fakeProduct().copy(createdAt = differentCreatedAt)
            )

            val testViewModel = createEditProductViewModel(
                productRepository = productRepository,
                shelfRepository = shelfRepository,
                productId = productId,
                dispatcher = dispatcher
            )
            advanceUntilIdle()

            val state = testViewModel.state.value
            assertEquals(fakeProduct().copy(createdAt = differentCreatedAt).name, state.productName)
        }

    @Test
    fun `getProductData - product with special characters in name SHOULD load successfully`() =
        scope.runTest {
            val specialName = "Product!@#$%^&*()_+-=[]{}|;':\",./<>?"
            setupProductRepositoryWithCustomProduct(
                productRepository,
                fakeProduct().copy(name = specialName)
            )

            val testViewModel = createEditProductViewModel(
                productRepository = productRepository,
                shelfRepository = shelfRepository,
                productId = productId,
                dispatcher = dispatcher
            )
            advanceUntilIdle()

            val state = testViewModel.state.value
            assertEquals(specialName, state.productName)
        }

    @Test
    fun `getProductData - product with unicode characters in description SHOULD load successfully`() =
        scope.runTest {
            val unicodeDescription = "وصف المنتج باللغة العربية".padEnd(120, 'ة')
            setupProductRepositoryWithCustomProduct(
                productRepository,
                fakeProduct().copy(description = unicodeDescription)
            )

            val testViewModel = createEditProductViewModel(
                productRepository = productRepository,
                shelfRepository = shelfRepository,
                productId = productId,
                dispatcher = dispatcher
            )
            advanceUntilIdle()

            val state = testViewModel.state.value
            assertEquals(unicodeDescription, state.description)
        }

    @Test
    fun `getProductData - product with many imageUrls SHOULD load all URLs`() = scope.runTest {
        val manyUrls = List(10) { "image$it.jpg" }
        setupProductRepositoryWithCustomProduct(
            productRepository,
            fakeProduct().copy(imageUrls = manyUrls)
        )

        val testViewModel = createEditProductViewModel(
            productRepository = productRepository,
            shelfRepository = shelfRepository,
            productId = productId,
            dispatcher = dispatcher
        )
        advanceUntilIdle()

        val state = testViewModel.state.value
        assertEquals(manyUrls.size, state.existingImageUrls.size)
    }

    @Test
    fun `getProductData - product when shelves empty SHOULD still load product data`() =
        scope.runTest {
            everySuspend { shelfRepository.getMyDukanShelves() } returns emptyList()

            val testViewModel = createEditProductViewModel(
                productRepository = productRepository,
                shelfRepository = shelfRepository,
                productId = productId,
                dispatcher = dispatcher
            )
            advanceUntilIdle()

            val state = testViewModel.state.value
            assertEquals(fakeProduct().name, state.productName)
            assertTrue(state.shelves.isEmpty())
            assertNull(state.selectedShelf)
        }

    @Test
    fun `onGetProductDataSuccess - when shelves are empty SHOULD not crash`() = scope.runTest {
        everySuspend { shelfRepository.getMyDukanShelves() } returns emptyList()

        val testViewModel = createEditProductViewModel(
            productRepository = productRepository,
            shelfRepository = shelfRepository,
            productId = productId,
            dispatcher = dispatcher
        )
        advanceUntilIdle()

        val state = testViewModel.state.value
        assertEquals(fakeProduct().name, state.productName)
        assertNull(state.selectedShelf)
    }

    @Test
    fun `onGetProductDataSuccess - when shelves are not empty SHOULD select product shelf`() =
        scope.runTest {
            val productWithShelf = fakeProduct().copy(shelfId = testShelfId)
            everySuspend { productRepository.getProductById(any()) } returns productWithShelf

            val testViewModel = createEditProductViewModel(
                productRepository = productRepository,
                shelfRepository = shelfRepository,
                productId = productId,
                dispatcher = dispatcher
            )

            advanceUntilIdle()

            val state = testViewModel.state.value
            assertEquals(productWithShelf.name, state.productName)
            assertTrue(state.shelves.isNotEmpty(), "Shelves should be loaded")
            assertNotNull(
                state.selectedShelf,
                "Product shelf should be selected when shelves are not empty"
            )
            assertEquals(testShelfId.toString(), state.selectedShelf.id)

            val shelfWithMatchingId = state.shelves.firstOrNull { it.id == testShelfId.toString() }
            assertNotNull(shelfWithMatchingId, "Shelf with product shelf ID should exist")
            assertTrue(shelfWithMatchingId.isSelected, "Matching shelf should be selected")
        }

    @Test
    fun `onGetProductDataSuccess - when shelves loaded before product data SHOULD select product shelf`() =
        scope.runTest {
            val productWithShelf = fakeProduct().copy(shelfId = testShelfId)
            everySuspend { productRepository.getProductById(any()) } returns productWithShelf
            everySuspend { shelfRepository.getMyDukanShelves() } returns fakeShelves()

            val testViewModel = createEditProductViewModel(
                productRepository = productRepository,
                shelfRepository = shelfRepository,
                productId = productId,
                dispatcher = dispatcher
            )

            advanceUntilIdle()

            val state = testViewModel.state.value
            assertEquals(productWithShelf.name, state.productName)
            assertTrue(state.shelves.isNotEmpty(), "Shelves should be loaded before product data")
            assertNotNull(
                state.selectedShelf,
                "Product shelf should be selected when shelves are loaded before product data"
            )
            assertEquals(testShelfId.toString(), state.selectedShelf.id)

            val shelfWithMatchingId = state.shelves.firstOrNull { it.id == testShelfId.toString() }
            assertNotNull(shelfWithMatchingId, "Shelf with product shelf ID should exist")
            assertTrue(shelfWithMatchingId.isSelected, "Matching shelf should be selected")
        }
}

@OptIn(ExperimentalUuidApi::class)
private val testShelfId = Uuid.parse("123e4567-e89b-12d3-a456-426614174000")
private const val DEFAULT_PRODUCT_ID = "test-product-id"

private fun createSavedStateHandle(productId: String = DEFAULT_PRODUCT_ID): SavedStateHandle {
    return SavedStateHandle(mapOf("productId" to productId))
}

private fun createEditProductViewModel(
    productRepository: ProductRepository,
    shelfRepository: ShelfRepository,
    productId: String = DEFAULT_PRODUCT_ID,
    dispatcher: CoroutineDispatcher = StandardTestDispatcher()
): EditProductViewModel {
    return EditProductViewModel(
        productRepository = productRepository,
        shelfRepository = shelfRepository,
        savedStateHandle = createSavedStateHandle(productId),
        dispatcher = dispatcher
    )
}

private fun setupProductRepositoryWithCustomProduct(
    productRepository: ProductRepository,
    product: Product
) {
    everySuspend { productRepository.getProductById(any()) } returns product
}

@OptIn(ExperimentalUuidApi::class)
private fun createSelectedShelfUi(shelf: Shelf = fakeShelves().first()): EditProductUiState.ShelfUiState {
    return EditProductUiState.ShelfUiState(
        id = shelf.id.toString(),
        name = shelf.name,
        isSelected = true
    )
}

private fun EditProductViewModel.updateStateWithValidProduct(
    productName: String = "Test Product",
    price: String = "50.0",
    priceAfterDiscount: String = "40.0",
    description: String = "Valid description".padEnd(120, 'x'),
    selectedShelf: EditProductUiState.ShelfUiState? = null,
    existingImageUrls: List<String> = listOf("existing-url"),
    images: List<EditProductUiState.ProductImageUi> = emptyList()
) {
    val shelf = selectedShelf ?: createSelectedShelfUi()
    updateState {
        copy(
            productName = productName,
            selectedShelf = shelf,
            shelves = shelves.map {
                if (it.id == shelf.id) shelf else it
            },
            price = price,
            priceAfterDiscount = priceAfterDiscount,
            description = description,
            existingImageUrls = existingImageUrls,
            images = images
        )
    }
}

private fun createSimpleShelfUi(
    id: String = "id1",
    name: String = "Shelf1",
    isSelected: Boolean = true
): EditProductUiState.ShelfUiState {
    return EditProductUiState.ShelfUiState(id = id, name = name, isSelected = isSelected)
}

private fun createValidProductImageUi(
    id: Long = 1234,
    image: ImageBitmap = mock<ImageBitmap>(),
    imageSizeInMegaByte: Double = 1.0,
    imageState: ProductImageState = ProductImageState.SUCCESS
): EditProductUiState.ProductImageUi {
    return EditProductUiState.ProductImageUi(
        id = id,
        image = image,
        imageSizeInMegaByte = imageSizeInMegaByte,
        imageState = imageState
    )
}

@OptIn(ExperimentalUuidApi::class)
private fun fakeProduct(): Product {
    return Product(
        id = Uuid.parse("013e0bb1-6177-4430-ae08-f3a1a24f6f7d"),
        name = "Test Product",
        description = "Test Description".padEnd(120, 'x'),
        price = Price(
            base = 99.99,
            final = 99.99
        ),
        shelfId = testShelfId,
        imageUrls = listOf("image1.jpg", "image2.jpg"),
        createdAt = "2025-09-16T15:06:57.507394",
        quantityInCart = 4,
        isFavorite = false
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

