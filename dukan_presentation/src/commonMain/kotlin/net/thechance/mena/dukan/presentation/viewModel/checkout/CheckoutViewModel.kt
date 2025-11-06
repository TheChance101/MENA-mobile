package net.thechance.mena.dukan.presentation.viewModel.checkout

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import net.thechance.mena.dukan.domain.repository.CartRepository
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class CheckoutViewModel(
    private val cartRepository: CartRepository,
    private val savedStateHandle: SavedStateHandle,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    BaseViewModel<CheckoutUiState, CheckoutEffect>(
        initialState = CheckoutUiState(),
        defaultDispatcher = dispatcher
    ), CheckoutInteractionListener {


    init {
        loadCartProductsFromRepository()
    }

    private fun loadCartProductsFromRepository() {
        tryToCollect(
            block = ::createPagingSource,
            onCollect = ::onProductsLoaded
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun createPagingSource(): Flow<PagingData<CartItem>> {
        val args = savedStateHandle.toRoute<DukanRoute.CheckoutScreenRoute>()
        return createPagingSourceFlow(
            mapper = { it.toUiState() }
        ) { pageNumber, pageSize ->
            cartRepository.getCartProducts(
                dukanId = Uuid.parse(args.dukanId),
                page = pageNumber,
                size = pageSize
            ).items
        }
    }

    private fun onProductsLoaded(products: PagingData<CartItem>) =
        updateState {
            copy(items = flowOf(products))
        }

    override fun onBackClicked() {
        emitEffect(effect = CheckoutEffect.NavigateBack)
    }

    override fun onConfirmOrderClicked() {
        // TODO add confirm order logic
    }

    override fun onChangeLocationClicked() {
        emitEffect(effect = CheckoutEffect.NavigateToChangeLocation)
    }

}