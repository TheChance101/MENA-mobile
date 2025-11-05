package net.thechance.mena.dukan.presentation.viewModel.productDetails

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel

class ProductDetailsViewModel(
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<ProductDetailsUiState, ProductDetailsEffects>(
    ProductDetailsUiState(),
    defaultDispatcher
), ProductDetailsInteractionListener {
    private val args = savedStateHandle.toRoute<DukanRoute.ProductDetails>()

    init {
        loadProductDetails()
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
                errorState = null
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
        //TODO
    }

    override fun onShareClicked() {
        //TODO
    }

    override fun onAddToFavoritesClicked() {
        //TODO
    }

    override fun onViewCartClicked() {
        //TODO
    }

}