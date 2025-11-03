package net.thechance.mena.dukan.presentation.viewModel.dukanCart

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import net.thechance.mena.dukan.domain.entity.Cart
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.model.UpdateProductCartQuantityParams
import net.thechance.mena.dukan.domain.repository.CartRepository
import net.thechance.mena.dukan.domain.repository.DukanManagementRepository
import net.thechance.mena.dukan.domain.repository.ProductRepository
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartUiState.CartState
import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartUiState.DukanInfoState
import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartUiState.ProductUiState

class DukanCartViewModel(
    private val cartRepository: CartRepository,
    private val dukanRepository: DukanManagementRepository,
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<DukanCartUiState, DukanCartEffects>(
    initialState = DukanCartUiState(),
    defaultDispatcher = defaultDispatcher
),
    DukanCartInteractionListener {
    private val dukanId = savedStateHandle.toRoute<DukanRoute.DukanCart>().dukanId
    private var productsMutableStateFlow =
        MutableStateFlow<PagingData<ProductUiState>>(PagingData.empty())

    init {
        loadCart()
    }

    private fun loadCart() {
        loadCartInfo()
        loadDukanInfo()
        loadProductsPaging()
    }

    private fun loadCartInfo() {
        tryToExecute(
            block = {
                cartRepository.getCartInfo(dukanId)
            },
            onSuccess = ::onLoadCartSuccess
        )
    }

    private fun onLoadCartSuccess(cart: Cart) {
        updateState {
            copy(
                totalPrice = cart.totalPrice,
                cartState = CartState.LOADED
            )
        }
    }


    private fun loadDukanInfo() {
        tryToExecute(
            onStart = {
                updateState {
                    copy(dukanInfoState = DukanInfoState.LOADING)
                }
            },
            block = {
                dukanRepository.getDukanDetailsByDukanId(dukanId)
            },
            onSuccess = ::onDukanInfoLoaded
        )
    }

    private fun onDukanInfoLoaded(dukan: Dukan) {
        updateState {
            copy(
                dukanInfo = dukan.toUiState(),
                dukanInfoState = DukanInfoState.LOADED
            )
        }
    }


    private fun loadProductsPaging() {
        tryToCollect(
            block = ::getProductPagingFlow,
            onCollect = ::onProductsLoaded
        )
    }

    private fun getProductPagingFlow(): Flow<PagingData<ProductUiState>> {
        return createPagingSourceFlow(mapper = { it.toUiState() }) { pageNumber, pageSize ->
            productRepository.getProductsCart(
                dukanId = dukanId,
                page = pageNumber,
                size = pageSize
            ).items
        }
    }

    private fun onProductsLoaded(products: PagingData<ProductUiState>) {
        productsMutableStateFlow.value = products
        updateState {
            copy(products = productsMutableStateFlow)
        }
    }


    override fun onBackClicked() {
        emitEffect(DukanCartEffects.NavigateBack)
    }

    override fun onDukanClicked() {
        emitEffect(DukanCartEffects.NavigateToDukanDetails(dukanId))
    }

    override fun onCheckoutClicked() {
        emitEffect(DukanCartEffects.NavigateToCheckout(dukanId))
    }

    override fun onIncreaseItemQuantityClicked(productId: String, newQuantity: Int) {
        updateProductQuantityInState(productId, newQuantity)
        updateProductQuantityInServer(productId, newQuantity)
    }

    override fun onDecreaseItemQuantityClicked(productId: String, newQuantity: Int) {
        if (newQuantity == 0) return
        updateProductQuantityInState(productId, newQuantity)
        updateProductQuantityInServer(productId, newQuantity)
    }

    private fun updateProductQuantityInState(productId: String, newQuantity: Int) {
        val updateProducts = productsMutableStateFlow.value.map {
            if (it.id == productId)
                it.copy(quantity = newQuantity)
            else it
        }

        productsMutableStateFlow.value = updateProducts
        updateState {
            copy(
                products = productsMutableStateFlow
            )
        }
    }

    private fun updateProductQuantityInServer(productId: String, newQuantity: Int) {
        tryToExecuteWithDebounce(
            block = {
                cartRepository.updateProductQuantity(
                    UpdateProductCartQuantityParams(
                        dukanId = dukanId,
                        productId = productId,
                        quantity = newQuantity
                    )
                )
            },
            onError = {},
            onSuccess = { loadCartInfo() }
        )
    }


    override fun onRemoveItemClicked(productId: String) {
        removeProductInState(productId)
        removeProductInServer(productId)
    }

    private fun removeProductInState(productId: String) {
        val updateProducts = productsMutableStateFlow.value.filter {
            it.id != productId
        }

        productsMutableStateFlow.value = updateProducts
        updateState {
            copy(
                products = productsMutableStateFlow
            )
        }
    }

    private fun removeProductInServer(productId: String) {
        tryToExecuteWithDebounce(
            block = {
                cartRepository.deleteProductFromCart(
                    dukanId = dukanId,
                    productId = productId
                )
            },
            onError = {},
            onSuccess = { loadCartInfo() }
        )
    }

    override fun onRetryLoadCartClicked() {
        loadCart()
    }
}