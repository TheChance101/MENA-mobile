package net.thechance.mena.wallet.presentation.screen.payment_result.args

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import net.thechance.mena.wallet.presentation.navigation.PaymentResultScreenRoute
import org.koin.core.annotation.Factory

interface PaymentResultArgs {
    val transactionId: String
    val submitTransactionResultStatus: String
    val receiverName: String
    val amount: Double
}

@Factory(binds = [PaymentResultArgs::class])
class PaymentResultArgsImpl(private val savedStateHandle: SavedStateHandle): PaymentResultArgs{
    override val transactionId: String
        get() = savedStateHandle.toRoute<PaymentResultScreenRoute>().transactionId
    override val submitTransactionResultStatus: String
        get() = savedStateHandle.toRoute<PaymentResultScreenRoute>().submitTransactionResultStatus
    override val receiverName: String
        get() = savedStateHandle.toRoute<PaymentResultScreenRoute>().receiverName
    override val amount: Double
        get() = savedStateHandle.toRoute<PaymentResultScreenRoute>().amount
}