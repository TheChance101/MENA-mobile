package net.thechance.mena.dukan.presentation.viewModel.productDetails

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.dukan.domain.entity.Product
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.productDetails.ProductDetailsArgs.PRODUCT_ID

class ProductDetailsViewModel(
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<ProductDetailsUiState, ProductDetailsEffects>(
    ProductDetailsUiState(),
    defaultDispatcher
), ProductDetailsInteractionListener {
    private val productId: String = requireNotNull(savedStateHandle[PRODUCT_ID])

    init {
        loadProductDetails()
    }

    private fun loadProductDetails() {
        updateState { copy(isLoading = true, isError = false) }
        tryToExecute(
            block = { productRepository.getProductDetails(productId) },
            onSuccess = ::onLoadProductSuccess,
            onError = ::onLoadProductError
        )
    }

    private fun onLoadProductSuccess(product: Product) {
        updateState {
            copy(
                isLoading = false,
                product = product.toUiState()
            )
        }
    }

    private fun onLoadProductError(throwable: Throwable) {
        updateState {
            copy(
                isLoading = false,
                isError = true
            )
        }
    }

    override fun onSecondaryImageClicked(imageUrl: String) {
        updateState { copy(selectedImageUrl = imageUrl) }
    }

    override fun onBackClicked() {
        emitEffect(ProductDetailsEffects.NavigateBack)
    }

    override fun onAddToCartClick(productId: String) {
        //TODO
    }

    override fun onShareButtonClicked() {
        //TODO
    }

    override fun onAddToFavoritesButtonClicked() {
        //TODO
    }

    override fun onViewCartButtonClicked() {
        //TODO
    }

}

object ProductDetailsArgs {
    const val PRODUCT_ID = "productId"
}