package net.thechance.mena.dukan.presentation.viewModel.editProduct

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.awaitCancellation
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
import mena.dukan_presentation.generated.resources.price_after_discount_bigger_than_base_price
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
import net.thechance.mena.dukan.domain.model.UpdateProductParams
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.domain.repository.ShelfRepository
import net.thechance.mena.dukan.presentation.component.product.productImage.ProductImageState
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.util.file.ImageFile
import net.thechance.mena.dukan.presentation.util.filterPriceInput
import net.thechance.mena.dukan.presentation.util.imageCrop.toPngByteArray
import net.thechance.mena.dukan.presentation.util.rounded
import net.thechance.mena.dukan.presentation.util.toFileName
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.createProduct.CreateProductUiState
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class EditProductViewModel(
    private val productRepository: ProductRepository,
    private val shelfRepository: ShelfRepository,
    savedStateHandle: SavedStateHandle,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<EditProductUiState, EditProductEffect>(
    initialState = EditProductUiState(productId = savedStateHandle.toRoute<DukanRoute.EditProductScreenRoute>().productId),
    defaultDispatcher = dispatcher
), EditProductInteractionListener {

    private val route: DukanRoute.EditProductScreenRoute = savedStateHandle.toRoute()
    private val productId: String = route.productId
    private var originalExistingUrls: List<String> = emptyList()
    private var originalProductName: String? = null
    private var productShelfId: String? = null

    init {
        getProductData()
        getShelves()
    }

    private fun getProductData() {
        tryToExecute(
            block = { productRepository.getProductById(productId) },
            onSuccess = ::onGetProductDataSuccess,
            onError = ::onGetProductDataError
        )
    }

    private fun onGetProductDataSuccess(product: Product) {
        val filteredImages = filterValidImageUrls(product.imageUrls)
        productShelfId = product.shelfId.toString()

        updateProductStateFromApi(product, filteredImages)
        saveOriginalProductData(product, filteredImages)

        if (state.value.shelves.isNotEmpty()) {
            selectProductShelf(product.shelfId.toString())
        }
    }

    private fun filterValidImageUrls(imageUrls: List<String>): List<String> {
        return imageUrls.filter { it.isNotBlank() }
    }

    private fun updateProductStateFromApi(product: Product, filteredImages: List<String>) {
        updateState {
            copy(
                productName = product.name,
                price = product.price.base.toString(),
                priceAfterDiscount = product.price.final.toString(),
                description = product.description,
                existingImageUrls = filteredImages,
                isTextFieldEnabled = true,
                isOutOfStock = product.isOutOfStock
            ).updateButtonState()
        }
    }

    private fun saveOriginalProductData(product: Product, filteredImages: List<String>) {
        originalExistingUrls = filteredImages
        originalProductName = product.name
    }

    private fun onGetProductDataError(throwable: Throwable) {
        val messageRes = when (throwable) {
            is NoInternetException -> Res.string.no_internet_connection
            is NoSuchItemException -> Res.string.error_product_not_found
            else -> Res.string.error_general
        }
        showSnackBar(message = messageRes, type = SnackBarType.ERROR)
    }

    private fun getShelves() {
        tryToExecute(
            onStart = { updateState { copy(isShelvesLoading = true) } },
            block = shelfRepository::getMyDukanShelves,
            onSuccess = ::onGetShelvesSuccess,
            onError = ::onErrorGettingShelves
        )
    }

    private fun onGetShelvesSuccess(shelves: List<Shelf>) {
        val shelvesUi = shelves.map { it.toUiState() }
        updateState {
            copy(
                shelves = shelvesUi,
                isShelvesLoading = false
            ).updateButtonState()
        }

        productShelfId?.let { shelfId ->
            selectProductShelf(shelfId)
        }
    }

    private fun selectProductShelf(shelfId: String) {
        if (state.value.shelves.isEmpty()) return

        val selected = state.value.shelves.firstOrNull { it.id == shelfId }
        updateState {
            copy(
                shelves = shelves.map { it.copy(isSelected = it.id == shelfId) },
                selectedShelf = selected
            ).updateButtonState()
        }
    }

    override fun onBackClicked() {
        emitEffect(effect = EditProductEffect.NavigateBack)
    }

    override fun onDeleteProductClicked() {
        updateState {
            copy(
                deleteDialog = EditProductUiState.DeleteDialogState(
                    title = Res.string.delete_product_title,
                    description = Res.string.delete_product_description
                )
            )
        }
    }

    override fun onDismissDeleteDialog() {
        updateState { copy(deleteDialog = null) }
    }

    override fun onDeleteConfirmed() {
        updateState { copy(deleteDialog = null) }
        tryToExecute(
            block = { productRepository.deleteProduct(productId) },
            onSuccess = ::onDeleteProductSuccess,
            onError = ::onDeleteProductError
        )
    }

    private fun onDeleteProductSuccess(unit: Unit) {
        showSnackBar(message = Res.string.delete_product_success, type = SnackBarType.SUCCESS)
        emitEffect(effect = EditProductEffect.NavigateToManageDukanProducts)
    }

    private fun onDeleteProductError(throwable: Throwable) {
        val messageRes = when (throwable) {
            is NoInternetException -> Res.string.no_internet_connection
            is NoSuchItemException -> Res.string.error_product_not_found
            is UnAuthorizedException -> Res.string.error_unauthorized_access
            else -> Res.string.error_delete_product
        }
        showSnackBar(message = messageRes, type = SnackBarType.ERROR)
    }

    override fun onProductNameChange(name: String) {
        updateState {
            copy(productName = name).updateButtonState()
        }
    }

    override fun onShelfSelect(shelfUiState: CreateProductUiState.ShelfUiState) {
        val editShelfUiState = EditProductUiState.ShelfUiState(
            id = shelfUiState.id,
            name = shelfUiState.name,
            isSelected = shelfUiState.isSelected
        )
        updateState {
            copy(
                shelves = shelves.map { shelfItem ->
                    shelfItem.copy(isSelected = shelfItem.id == shelfUiState.id)
                },
                selectedShelf = editShelfUiState,
            ).updateButtonState()
        }
    }

    override fun onPriceChange(price: String) {
        updateState {
            copy(
                price = filterPriceInput(price)
            ).updateButtonState()
        }
    }

    override fun onPriceAfterDiscountChange(price: String) {
        updateState {
            copy(
                priceAfterDiscount = filterPriceInput(price)
            ).updateButtonState()
        }
    }

    override fun onDescriptionChange(description: String) {
        updateState {
            copy(description = description).updateButtonState()
        }
    }

    override fun onOutOfStockChange(isOutOfStock: Boolean) {
        updateState {
            copy(isOutOfStock = isOutOfStock).updateButtonState()
        }
    }

    override fun onUploadImageClicked(image: ImageFile) {
        tryToExecute(
            block = { onUploadImageBlock(image) },
            onError = ::onErrorUploadingImages
        )
    }

    private suspend fun onUploadImageBlock(image: ImageFile) {
        if (isUploadImageValidToCrop(image).not())
            awaitCancellation()

        val imageSrc = image.toImageSrc()
        updateState {
            copy(
                selectedImage = imageSrc,
                showCropImage = true
            )
        }
    }

    private suspend fun isUploadImageValidToCrop(image: ImageFile): Boolean {
        val imageSizeInMegabyte = calculateImageSizeInMegabyte(image)
        val imageBitmap = image.toImageBitmap()
        val imageAspectRatio = calculateImageAspectRatio(imageBitmap)
        val totalImagesCount = calculateTotalImagesCount()

        return when {
            !isImageCountWithinLimit(totalImagesCount) -> handleUploadImageError(Res.string.error_image_max_limit)
            !isImageSizeWithinLimit(imageSizeInMegabyte) -> handleUploadImageError(Res.string.error_image_size)
            !isImageSourceValid(image) -> handleUploadImageError(Res.string.error_upload_failed)
            isImageAspectRatioMatched(imageAspectRatio) -> addImageToList(
                imageBitmap,
                imageSizeInMegabyte
            )

            else -> true
        }
    }

    private suspend fun calculateImageSizeInMegabyte(image: ImageFile): Double {
        return image.size().toDouble() / BYTES_PER_MEGABYTE
    }

    private fun calculateImageAspectRatio(imageBitmap: ImageBitmap): Float {
        return imageBitmap.width.toFloat() / imageBitmap.height.toFloat()
    }

    private fun calculateTotalImagesCount(): Int {
        return state.value.images.size + state.value.existingImageUrls.size
    }

    private fun isImageCountWithinLimit(totalImagesCount: Int): Boolean {
        return totalImagesCount < IMAGE_MAX_LIMIT
    }

    private fun isImageSizeWithinLimit(imageSizeInMegabyte: Double): Boolean {
        return imageSizeInMegabyte <= IMAGE_MAX_SIZE_IN_MB
    }

    private suspend fun isImageSourceValid(image: ImageFile): Boolean {
        return image.toImageSrc() != null
    }

    private fun isImageAspectRatioMatched(imageAspectRatio: Float): Boolean {
        return imageAspectRatio == IMAGE_ASPECT_RATIO
    }

    private fun handleUploadImageError(resErrorMessage: StringResource): Boolean {
        showSnackBar(message = resErrorMessage, type = SnackBarType.ERROR)
        updateState {
            copy(
                showCropImage = false,
                selectedImage = null
            )
        }
        return false
    }

    private fun addImageToList(imageBitmap: ImageBitmap, imageSizeInMegabyte: Double): Boolean {
        updateState {
            copy(
                images = images + EditProductUiState.ProductImageUi(
                    image = imageBitmap,
                    imageSizeInMegaByte = imageSizeInMegabyte.rounded(),
                    imageState = ProductImageState.SUCCESS,
                )
            ).updateButtonState()
        }
        return false
    }

    fun onCroppedImage(imageBitmap: ImageBitmap) {
        tryToExecute(
            block = { onCroppedImageBlock(imageBitmap) },
            onSuccess = ::onCroppedImageSuccess,
        )
    }

    private fun onCroppedImageBlock(imageBitmap: ImageBitmap): EditProductUiState.ProductImageUi {
        updateState {
            copy(
                selectedImage = null,
                showCropImage = false
            )
        }
        val imageByteArray = imageBitmap.toPngByteArray().size.toDouble()
        val imageSizeInMegabyte = imageByteArray / BYTES_PER_MEGABYTE

        return EditProductUiState.ProductImageUi(
            image = imageBitmap,
            imageSizeInMegaByte = imageSizeInMegabyte.rounded(),
            imageState = ProductImageState.SUCCESS,
        )
    }

    private fun onCroppedImageSuccess(productImage: EditProductUiState.ProductImageUi) {
        updateState {
            copy(images = images + productImage).updateButtonState()
        }
    }

    override fun onCropImageBackClicked() {
        updateState {
            copy(
                selectedImage = null,
                showCropImage = false
            )
        }
    }

    override fun onCancelImageClicked(image: ImageBitmap) {
        updateState {
            copy(
                images = images.filter { it.image != image }
            ).updateButtonState()
        }
    }

    override fun onCancelExistingImageUrl(url: String) {
        updateState {
            copy(existingImageUrls = existingImageUrls.filterNot { it == url })
                .updateButtonState()
        }
    }

    override fun onSaveProductClicked() {
        tryToExecute(
            block = ::onSaveProductBlock,
            onSuccess = { onSaveProductSuccess() },
            onError = ::onSaveProductError
        )
    }

    private suspend fun onSaveProductBlock() {
        validateProductBeforeSave()
        updateUiStateForSaving()

        val existingImageUrls = state.value.existingImageUrls
        val uploadedUrls = uploadNewImages()
        val finalImageUrls = existingImageUrls + uploadedUrls

        validateFinalImageUrls(finalImageUrls)
        updateProductData(finalImageUrls)
        updateStateAfterSave(finalImageUrls)
    }

    private suspend fun validateProductBeforeSave() {
        val productErrorMessage = getProductValidationError(productUiState = state.value)
        if (isProductDetailsValid(productErrorMessage).not()) awaitCancellation()

        if (isProductValid(state.value).not()) {
            showSnackBar(message = Res.string.error_general, type = SnackBarType.ERROR)
            awaitCancellation()
        }
    }

    private fun updateUiStateForSaving() {
        updateState {
            copy(
                images = images.map { it.copy(imageState = ProductImageState.LOADING) },
                isSaveButtonLoading = true,
                isUploadingImageEnabled = false,
                isTextFieldEnabled = false,
                isCancelImageEnabled = false
            )
        }
    }

    private suspend fun uploadNewImages(): List<String> {
        val newBitmaps = extractNewImageBitmaps()
        val uploadedUrls = mutableListOf<String>()

        for (bitmap in newBitmaps) {
            val imageUrl = uploadSingleImage(bitmap)
            uploadedUrls.add(imageUrl)
        }

        return uploadedUrls
    }

    private fun extractNewImageBitmaps(): List<ImageBitmap> {
        return state.value.images.map { it.image }
    }

    private suspend fun uploadSingleImage(bitmap: ImageBitmap): String {
        val bytes = bitmap.toPngByteArray()
        val fileName = bytes.toFileName()
        val result = productRepository.uploadProductImage(
            fileName = fileName,
            fileBytes = bytes,
            productId = productId
        )
        return result
    }

    private suspend fun validateFinalImageUrls(finalImageUrls: List<String>) {
        if (finalImageUrls.isEmpty()) {
            resetUiStateAfterValidationFailure()
            showSnackBar(message = Res.string.error_general, type = SnackBarType.ERROR)
            awaitCancellation()
        }
    }

    private fun resetUiStateAfterValidationFailure() {
        updateState {
            copy(
                isSaveButtonLoading = false,
                isUploadingImageEnabled = true,
                isTextFieldEnabled = true,
                isCancelImageEnabled = true
            )
        }
    }

    private suspend fun updateProductData(finalImageUrls: List<String>) {
        val params = buildUpdateProductParams(finalImageUrls)
        productRepository.updateProduct(productId = productId, params = params)
    }

    private fun buildUpdateProductParams(finalImageUrls: List<String>): UpdateProductParams {
        val trimmedName = state.value.productName.trim()
        val trimmedDescription = state.value.description.trim()

        return UpdateProductParams(
            name = trimmedName,
            description = trimmedDescription,
            price = Price(
                base = state.value.price.toDoubleOrNull() ?: 0.0,
                final = state.value.priceAfterDiscount.toDoubleOrNull()
            ),
            shelfId = state.value.selectedShelf?.id,
            imageUrls = finalImageUrls,
            isOutOfStock = state.value.isOutOfStock
        )
    }

    private fun updateStateAfterSave(finalImageUrls: List<String>) {
        updateProductImagesState(finalImageUrls)
        saveOriginalDataAfterUpdate(finalImageUrls)
    }

    private fun updateProductImagesState(finalImageUrls: List<String>) {
        updateState {
            copy(
                images = images.map { it.copy(imageState = ProductImageState.SUCCESS) },
                existingImageUrls = finalImageUrls
            )
        }
    }

    private fun saveOriginalDataAfterUpdate(finalImageUrls: List<String>) {
        val trimmedName = state.value.productName.trim()
        originalExistingUrls = finalImageUrls
        originalProductName = trimmedName
    }

    private fun onSaveProductSuccess() {
        showSnackBar(message = Res.string.save_product_success, type = SnackBarType.SUCCESS)
        updateState {
            copy(
                isSaveButtonLoading = false,
                images = images.map { it.copy(imageState = ProductImageState.SUCCESS) },
            )
        }
        emitEffect(effect = EditProductEffect.NavigateToManageDukanProducts)
    }

    private fun onSaveProductError(throwable: Throwable) {
        val errorMessage = getSaveProductErrorMessage(throwable)
        showSnackBar(message = errorMessage, type = SnackBarType.ERROR)
        resetUiStateAfterSaveError()
    }

    private fun getSaveProductErrorMessage(throwable: Throwable): StringResource {
        return when (throwable) {
            is NoInternetException -> Res.string.no_internet_connection
            is DuplicateNameException -> Res.string.product_name_is_already_exist
            is NoSuchItemException -> Res.string.error_product_not_found
            is UnAuthorizedException -> Res.string.error_unauthorized_access
            is CreationFailedException -> Res.string.error_update_product
            is UploadingFailedException -> Res.string.error_upload_failed
            is InvalidImageFormatException -> Res.string.invalid_image_format
            else -> Res.string.error_general
        }
    }

    private fun resetUiStateAfterSaveError() {
        updateState {
            copy(
                images = images.map { it.copy(imageState = ProductImageState.SUCCESS) },
                isSaveButtonLoading = false,
                isUploadingImageEnabled = true,
                isTextFieldEnabled = true,
                isCancelImageEnabled = true
            )
        }
    }

    override fun onDismissSnackBar() {
        updateState {
            copy(
                showSnackBar = false,
                snackBarUiState = null
            )
        }
    }

    private fun onErrorGettingShelves(throwable: Throwable) {
        val messageRes = when (throwable) {
            is NoInternetException -> Res.string.no_internet_connection
            else -> Res.string.error_general
        }
        showSnackBar(message = messageRes, type = SnackBarType.ERROR)
        updateState { copy(isShelvesLoading = false) }
    }

    private fun onErrorUploadingImages(throwable: Throwable) {
        val messageRes = when (throwable) {
            is NoInternetException -> Res.string.no_internet_connection
            is UploadingFailedException -> Res.string.error_upload_failed
            is InvalidImageFormatException -> Res.string.invalid_image_format
            else -> Res.string.error_general
        }
        showSnackBar(message = messageRes, type = SnackBarType.ERROR)
    }

    private fun EditProductUiState.updateButtonState(): EditProductUiState {
        return copy(isSaveButtonEnabled = isProductValid(this))
    }

    private fun isProductDetailsValid(productErrorMessage: StringResource?): Boolean {
        return if (productErrorMessage != null) {
            showSnackBar(message = productErrorMessage, type = SnackBarType.ERROR)
            false
        } else {
            true
        }
    }

    private fun isProductValid(productUiState: EditProductUiState): Boolean {
        val hasAnyImage =
            productUiState.images.isNotEmpty() || productUiState.existingImageUrls.isNotEmpty()
        return productUiState.productName.trim().isNotEmpty() &&
                productUiState.selectedShelf != null &&
                productUiState.price.isNotEmpty() &&
                productUiState.description.trim().isNotEmpty() &&
                hasAnyImage
    }

    private fun showSnackBar(message: StringResource, type: SnackBarType) {
        updateState {
            copy(
                snackBarUiState = SnackBarUiState(
                    message = message,
                    snackBarType = type
                ),
                showSnackBar = true
            )
        }
    }

    private fun getProductValidationError(productUiState: EditProductUiState): StringResource? {
        val basePrice = productUiState.price.toDoubleOrNull()
        val discountPrice = productUiState.priceAfterDiscount.toDoubleOrNull()

        return when {
            basePrice == null -> Res.string.error_price_invalid
            basePrice <= PRICE_EXCLUSIVE_LOWER_BOUND -> Res.string.error_price_not_positive
            productUiState.description.length !in MIN_DESCRIPTION_LENGTH..MAX_DESCRIPTION_LENGTH ->
                Res.string.error_description_length
            discountPrice != null && discountPrice > basePrice ->
                Res.string.price_after_discount_bigger_than_base_price
            else -> null
        }
    }

    companion object {
        const val IMAGE_ASPECT_RATIO = 1F
        const val IMAGE_MAX_LIMIT = 10
        const val IMAGE_MAX_SIZE_IN_MB = 5
        const val BYTES_PER_MEGABYTE = 1024 * 1024
        const val MIN_DESCRIPTION_LENGTH = 25
        const val MAX_DESCRIPTION_LENGTH = 3000
        const val PRICE_EXCLUSIVE_LOWER_BOUND = 0.0
    }
}
