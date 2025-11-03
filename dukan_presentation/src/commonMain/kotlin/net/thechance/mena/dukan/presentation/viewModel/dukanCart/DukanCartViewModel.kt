package net.thechance.mena.dukan.presentation.viewModel.dukanCart

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import net.thechance.mena.dukan.domain.entity.Cart
import net.thechance.mena.dukan.domain.entity.Dukan
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
        loadDukanCart()
        loadDukanInfo()
        loadProductsPaging()
    }

    private fun loadDukanCart() {
        tryToExecute(
            onStart = {
                updateState {
                    copy(cartState = CartState.LOADING)
                }
            },
            block = {
                cartRepository.getCartInfo(dukanId)
            },
            onSuccess = ::onCartLoaded
        )
    }

    private fun onCartLoaded(cart: Cart) {
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
            onStart = {
                updateState {
                    copy(cartState = CartState.LOADING)
                }
            },
            block = ::getProductPagingFlow,
            onCollect = ::onProductsLoaded
        )
    }

    private fun onProductsLoaded(products: PagingData<ProductUiState>) {
        productsMutableStateFlow = MutableStateFlow(products)
        updateState {
            copy(
                products = productsMutableStateFlow,
                cartState = CartState.LOADED
            )
        }
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

    override fun onBackClicked() {
        emitEffect(DukanCartEffects.NavigateBack)
    }

    override fun onDukanClicked() {
        emitEffect(DukanCartEffects.NavigateToDukanDetails(dukanId))
    }

    override fun onCheckoutClicked() {
        emitEffect(DukanCartEffects.NavigateToCheckout(dukanId))
    }

    override fun onIncreaseItemQuantityClicked(cartItemId: String) {
    }

    override fun onDecreaseItemQuantityClicked(cartItemId: String) {
    }

    override fun onRemoveItemClicked(cartItemId: String) {
    }

    override fun onRetryLoadCartClicked() {
        loadCart()
    }
}