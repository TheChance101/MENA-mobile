package net.thechance.mena.wallet.presentation.screen.confirm_payment.args

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import net.thechance.mena.wallet.presentation.navigation.ConfirmPaymentScreenRoute
import org.koin.core.annotation.Factory

interface ConfirmPaymentArgs {
    val transactionId: String
}

@Factory(binds = [ConfirmPaymentArgs::class])
class ConfirmPaymentArgsImpl(private val savedStateHandle: SavedStateHandle) : ConfirmPaymentArgs {
    override val transactionId: String
        get() = savedStateHandle.toRoute<ConfirmPaymentScreenRoute>().transactionId
}