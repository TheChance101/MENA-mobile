package net.thechance.mena.dukan.presentation.viewModel.orderDetails

sealed interface OrderDetailsEffect {
    object NavigateBack : OrderDetailsEffect
}