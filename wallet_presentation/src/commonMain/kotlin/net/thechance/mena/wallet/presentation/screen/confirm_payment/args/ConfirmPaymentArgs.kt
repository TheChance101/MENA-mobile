package net.thechance.mena.wallet.presentation.screen.confirm_payment.args

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import net.thechance.mena.wallet.presentation.navigation.ConfirmPaymentScreenRoute
import org.koin.core.annotation.Factory

interface ConfirmPaymentArgs {
    val transactionId: String
    val amount: Double
}

@Factory(binds = [ConfirmPaymentArgs::class])
class ConfirmPaymentArgsImpl(private val savedStateHandle: SavedStateHandle) : ConfirmPaymentArgs {
    override val transactionId: String
        get() = savedStateHandle.toRoute<ConfirmPaymentScreenRoute>().transactionId

    override val amount: Double
        get() = savedStateHandle.toRoute<ConfirmPaymentScreenRoute>().amount
}