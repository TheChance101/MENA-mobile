package net.thechance.mena.dukan.presentation.viewModel.checkout

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class CheckoutUiState(
    val cartId: Uuid = Uuid.random(),
    val dukanId: String = "",
    val deliveryAddress: Address = Address(),
    val items: Flow<PagingData<CartItem>> = emptyFlow(),
    val cartDetails: CartDetails = CartDetails(),
    val snackBarState: SnackBarUiState? = null,
    val transaction: TransactionUiState = TransactionUiState(),
    val isTransactionLoading: Boolean = false,
    val checkoutStatus: CheckoutStatus = CheckoutStatus.LOADING,
    val isConfirmOrderButtonEnabled: Boolean = false
) {
    data class Address(
        val label: AddressLabel = AddressLabel.Home,
        val street: String = "",
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
    )

    enum class AddressLabel {
        Home, Office, Other;
    }

    data class CartDetails(
        val discountPercentage: Double = 0.0,
        val platformFees: Double = 0.0,
        val totalPriceBeforeDiscount: Double = 0.0,
        val totalPriceAfterDiscount: Double = 0.0
    )

    data class CartItem(
        val id: String = "",
        val name: String = "",
        val quantity: Int = 0,
        val price: Double = 0.0
    )

    data class TransactionUiState(
        val transactionId: Uuid = Uuid.random(),
        val amount: Double = 0.0
    )
    enum class CheckoutStatus {
        LOADING,
        LOADED,
        NO_INTERNET
    }
}