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
import mena.dukan_presentation.generated.resources.added_to_favorites
import mena.dukan_presentation.generated.resources.error_updating_favorites
import mena.dukan_presentation.generated.resources.no_internet_connection
import mena.dukan_presentation.generated.resources.removed_from_favorites
import net.thechance.mena.dukan.domain.entity.Cart
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.exceptions.NoInternetException
import net.thechance.mena.dukan.domain.exceptions.NoSuchItemException
import net.thechance.mena.dukan.domain.model.UpdateProductCartQuantityParams
import net.thechance.mena.dukan.domain.repository.CartRepository
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import org.jetbrains.compose.resources.StringResource

class ProductDetailsViewModel(
    private val productRepository: ProductRepository,
    private val dukanCartRepository: CartRepository,
    savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<ProductDetailsUiState, ProductDetailsEffects>(
    ProductDetailsUiState(),
    defaultDispatcher
), ProductDetailsInteractionListener {
    private val args = savedStateHandle.toRoute<DukanRoute.ProductDetails>()

    init {
        loadProductDetails()
        loadCartInfo()
    }

    private fun loadCartInfo() {
        tryToExecute(
            block = { dukanCartRepository.getCartInfo(args.dukanId) },
            onError = ::onCartInfoError,
            onSuccess = ::onLoadCartSuccess
        )
    }

    private fun onCartInfoError(throwable: Throwable) {
        when (throwable) {
            is NoSuchItemException -> updateState { copy(totalPrice = 0.0) }
            is NoInternetException -> updateState { copy(totalPrice = 0.0) }
            else -> updateState { copy(totalPrice = 0.0) }
        }
    }

    private fun onLoadCartSuccess(cart: Cart) {
        updateState {
            copy(
                totalPrice = cart.totalPrice,
            )
        }
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
        updateState { copy(isFirstQuantityOne = productUiInfo.inCartQuantity == 0) }
        updateState {
            copy(
                isLoading = false,
                product = productUiInfo,
                selectedImageUrl = productUiInfo.images.firstOrNull() ?: "",
                errorState = null,
                isFavorite = product.isFavorite
            )
        }
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

    override fun onSecondaryImageClicked(imageUrl: String) {
        updateState { copy(selectedImageUrl = imageUrl) }
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
            block = { addToCartBlock(domainRequest) },
            onSuccess = ::addProductToCartSuccessfully,
            onError = ::onErrorUpdateProductQuantity
        )
    }

    private suspend fun addToCartBlock(domainRequest: UpdateProductCartQuantityParams) {
        if (state.value.isFirstQuantityOne) dukanCartRepository.addProductQuantity(domainRequest)
        else dukanCartRepository.updateProductQuantity(domainRequest)
    }

    override fun onPlusClicked(productId: String) {
        viewModelScope.launch(Dispatchers.Main) {
            updateState { copy(product.copy(inCartQuantity = product.inCartQuantity + 1)) }
        }
    }

    override fun onMinusClicked(productId: String) {
        viewModelScope.launch(Dispatchers.Main) {
            updateState {
                copy(product.copy(inCartQuantity = if (product.inCartQuantity > 0) product.inCartQuantity - 1 else product.inCartQuantity))
            }
        }
    }

    private fun onErrorUpdateProductQuantity(throwable: Throwable) {
        updateState { copy(isAddToCartLoading = false) }
        if (throwable is NoInternetException) {
            showSnackBar(
                message = Res.string.no_internet_connection,
                type = SnackBarType.ERROR
            )
        }
    }

    private fun addProductToCartSuccessfully(success: Unit) {
        updateState { copy(isAddToCartLoading = false) }
        val messageRes = Res.string.add_product_success
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

        tryToExecute(
            block = { productRepository.toggleProductToFavorites(currentProduct.id) },
            onSuccess = {updateState { copy(isFavorite = isCurrentlyFavorite) }},
        )
    }

    fun refreshData(){
        loadProductDetails()
        loadCartInfo()
    }
}