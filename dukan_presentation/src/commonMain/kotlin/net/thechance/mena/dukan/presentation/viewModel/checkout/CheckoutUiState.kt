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
    val discountPercentage: Int = 0,
    val platformFees: Double = 0.0,
    val totalAmount: Double = 0.0,
    val snackBarState: SnackBarUiState? = null,
    val transaction: TransactionUiState = TransactionUiState(),
    val isTransactionLoading: Boolean = false,
    val isConfirmOrderButtonEnabled: Boolean = false
) {
    data class Address(
        val label: AddressLabel = AddressLabel.Home,
        val street: String = "",
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
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

    data class TransactionUiState(
        val transactionId: Uuid = Uuid.random(),
        val amount: Double = 0.0
    )
}