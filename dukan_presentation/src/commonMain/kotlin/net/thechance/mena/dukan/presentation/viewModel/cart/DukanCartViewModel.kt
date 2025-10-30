package net.thechance.mena.dukan.presentation.viewModel.cart

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.dukan.presentation.screen.dukanCart.DukanCartArgs.DUKAN_ID
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel

class DukanCartViewModel(
    savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<DukanCartUiState, DukanCartEffects>(initialState = DukanCartUiState()), DukanCartInteractionListener {
    val dukanId: String = requireNotNull(savedStateHandle[DUKAN_ID])

    override fun onBackClick() {
        emitEffect(DukanCartEffects.NavigateBack)
    }

    override fun onDukanDetailsClick(dukanId: String) {
        emitEffect(DukanCartEffects.NavigateToDukanDetails(dukanId))
    }

    override fun onCheckoutClick(dukanId: String) {
        emitEffect(DukanCartEffects.NavigateToCheckout(dukanId))
    }

    override fun onIncreaseItemQuantityClick(cartItemId: String) {
        TODO("Not yet implemented")
    }

    override fun onDecreaseItemQuantityClick(cartItemId: String) {
        TODO("Not yet implemented")
    }

    override fun onRemoveItemClick(cartItemId: String) {
        TODO("Not yet implemented")
    }

    override fun onRetryLoadCartClick() {
        TODO("Not yet implemented")
    }
}