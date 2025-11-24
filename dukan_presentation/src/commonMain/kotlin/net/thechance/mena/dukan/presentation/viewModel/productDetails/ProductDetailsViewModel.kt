package net.thechance.mena.dukan.presentation.viewModel.productDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_product_success
import mena.dukan_presentation.generated.resources.no_internet_connection
import mena.dukan_presentation.generated.resources.remove_product_successfully
import mena.dukan_presentation.generated.resources.something_went_wrong
import net.thechance.mena.dukan.domain.entity.Cart
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.model.UpdateProductCartQuantityParams
import net.thechance.mena.dukan.domain.repository.CartRepository
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import org.jetbrains.compose.resources.StringResource

class ProductDetailsViewModel(
    private val productRepository: ProductRepository,
    private val dukanCartRepository: CartRepository,
    private val dukanManagementRepository: DukanManagementRepository,
    savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<ProductDetailsUiState, ProductDetailsEffects>(
    ProductDetailsUiState(),
    defaultDispatcher
), ProductDetailsInteractionListener {
    private val args = savedStateHandle.toRoute<DukanRoute.ProductDetails>()

    private var previousProductQuantity: Int = 0


    init {
        loadProductDetails()
        loadCartInfo()
        loadDukanInfo()
    }

    private fun loadDukanInfo() {
        tryToExecute(
            block = { dukanManagementRepository.getDukanDetailsByDukanId(args.dukanId) },
            onSuccess = ::onLoadDukanSuccess,
            onError = ::onLoadDukanError
        )
    }

    private fun onLoadDukanSuccess(dukan: Dukan) {
        updateState { copy(dukanColor = parseHexColor(color = dukan.color.hexCode)) }
    }

    private fun onLoadDukanError(throwable: Throwable) {
        updateState { copy(dukanColor = 0xFF000000) }
    }

    private fun loadCartInfo() {
        tryToExecute(
            block = { dukanCartRepository.getCartInfo(args.dukanId) },
            onError = ::onCartInfoError,
            onSuccess = ::onLoadCartSuccess
        )
    }

    private fun onCartInfoError(throwable: Throwable) {
        updateState { copy(hasProductInCart = false) }
    }

    private fun onLoadCartSuccess(cart: Cart) {
        updateState { copy(hasProductInCart = cart.totalPriceAfterDiscount > 0.0) }
    }

    private fun loadProductDetails() {
        tryToExecute(
            onStart = { updateState { copy(isLoading = true) } },
            block = { productRepository.getProductDetails(productId = args.productId) },
            onSuccess = ::onLoadProductSuccess,
            onError = ::onLoadProductError
        )
    }

    private fun onLoadProductSuccess(product: Product) {
        val productUiInfo = product.toUiState()
        updateState {
            copy(
                isLoading = false,
                product = productUiInfo,
                selectedImageUrl = productUiInfo.images.firstOrNull() ?: "",
                errorState = null,
                isFavorite = product.isFavorite,
                isFirstQuantityOne = productUiInfo.inCartQuantity == 0
            )
        }
        previousProductQuantity = product.quantityInCart
    }

    private fun onLoadProductError(throwable: Throwable) {
        updateState {
            copy(
                isLoading = false,
                errorState = throwable as? Exception
            )
        }
    }

    override fun onRetryClicked() {
        loadProductDetails()
    }

    override fun onSecondaryImageClicked(imageUrl: String, selectedImageUrl: String) {
        if (imageUrl != selectedImageUrl) {
            updateState { copy(selectedImageUrl = imageUrl) }
        }
    }

    override fun onBackClicked() {
        emitEffect(ProductDetailsEffects.NavigateBack)
    }

    override fun onAddToCartClicked(productId: String) {

        val productQuantity = state.value.product.inCartQuantity

        val uiRequest =
            ProductDetailsUiState.ProductInfo(id = productId, inCartQuantity = productQuantity)
        val domainRequest = uiRequest.toDomainParams(dukanId = args.dukanId)

        tryToExecute(
            onStart = { updateState { copy(isAddToCartLoading = true) } },
            block = { onAddToCartBlock(domainRequest) },
            onSuccess = ::onSuccessUpdateProductQuantity,
            onError = ::onErrorUpdateProductQuantity
        )
    }

    private suspend fun onAddToCartBlock(domainRequest: UpdateProductCartQuantityParams) {
        if (state.value.product.inCartQuantity == 0) removeProductFromCart()
        else addProductToCart(domainRequest)
        refreshCartInfo()
    }

    private suspend fun addProductToCart(domainRequest: UpdateProductCartQuantityParams) {
        if (state.value.isFirstQuantityOne) dukanCartRepository.addProductQuantity(domainRequest)
        else dukanCartRepository.updateProductQuantity(domainRequest)
    }

    private suspend fun removeProductFromCart() {
        dukanCartRepository.deleteProductFromCart(
            dukanId = args.dukanId,
            productId = args.productId
        )
        updateState { copy(isFirstQuantityOne = true) }
    }

    override fun onPlusClicked(productId: String) {
        viewModelScope.launch(Dispatchers.Main) {
            updateState { copy(product.copy(inCartQuantity = product.inCartQuantity + 1)) }
            updateAddToCartButtonIsEnable()
        }
    }

    override fun onMinusClicked(productId: String) {
        viewModelScope.launch(Dispatchers.Main) {
            updateState {
                copy(product.copy(inCartQuantity = if (product.inCartQuantity > 0) product.inCartQuantity - 1 else product.inCartQuantity))
            }
            updateAddToCartButtonIsEnable()
        }
    }

    private fun updateAddToCartButtonIsEnable() {
        updateState { copy(isButtonEnable = product.inCartQuantity != previousProductQuantity) }
    }

    private fun onErrorUpdateProductQuantity(throwable: Throwable) {
        updateState { copy(isAddToCartLoading = false) }
        val messageRes = when (throwable) {
            is NoInternetException -> Res.string.no_internet_connection
            else -> Res.string.something_went_wrong
        }
        showSnackBar(message = messageRes, type = SnackBarType.ERROR)
    }

    private fun onSuccessUpdateProductQuantity(success: Unit) {
        previousProductQuantity = state.value.product.inCartQuantity
        if (state.value.product.inCartQuantity == 0) removeProductFromCartSuccessfully()
        else addProductToCartSuccessfully()

    }

    private fun addProductToCartSuccessfully() {
        updateState {
            copy(
                isAddToCartLoading = false,
                isButtonEnable = product.inCartQuantity != previousProductQuantity
            )
        }
        val messageRes = Res.string.add_product_success
        showSnackBar(message = messageRes, type = SnackBarType.SUCCESS)
    }

    private fun removeProductFromCartSuccessfully() {
        updateState {
            copy(
                isAddToCartLoading = false,
                isButtonEnable = product.inCartQuantity != previousProductQuantity
            )
        }
        val messageRes = Res.string.remove_product_successfully
        showSnackBar(message = messageRes, type = SnackBarType.SUCCESS)
    }

    private fun showSnackBar(message: StringResource, type: SnackBarType) {
        updateState {
            copy(
                snackBarState = SnackBarUiState(
                    message = message,
                    snackBarType = type
                )
            )
        }
    }

    override fun onDismissSnackBar() {
        updateState {
            copy(
                snackBarState = null
            )
        }
    }

    override fun onViewCartClicked() {
        emitEffect(ProductDetailsEffects.NavigateToCart(args.dukanId))
    }

    override fun onToggleProductToFavoriteClicked() {
        val currentProduct = state.value.product
        val isCurrentlyFavorite = state.value.isFavorite
        updateState { copy(isFavorite = !isCurrentlyFavorite) }
        tryToExecute(
            block = { productRepository.toggleProductToFavorites(currentProduct.id) },
        )
    }

    fun refreshCartInfo(){
        loadCartInfo()
    }
}