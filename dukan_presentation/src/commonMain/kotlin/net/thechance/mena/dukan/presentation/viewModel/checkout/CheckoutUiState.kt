package net.thechance.mena.dukan.presentation.viewModel.checkout

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState

data class CheckoutUiState(
    val deliveryAddress: Address = Address(),
    val items: Flow<PagingData<CartItem>> = emptyFlow(),
    val isCheckoutImplementedDialogVisible: Boolean = false,
    val discountPercentage: Int = 0,
    val platformFees: Double = 0.0,
    val totalAmount: Double = 0.0,
    val snackBarState: SnackBarUiState? = null,
    ) {
    data class Address(
        val label: AddressLabel = AddressLabel.Home,
        val street: String = ""
    )
    enum class AddressLabel{
        Home, Office, Other;
    }

    data class CartItem(
        val id: String = "",
        val name: String = "",
        val quantity: Int = 0,
        val price: Double = 0.0
    )
}