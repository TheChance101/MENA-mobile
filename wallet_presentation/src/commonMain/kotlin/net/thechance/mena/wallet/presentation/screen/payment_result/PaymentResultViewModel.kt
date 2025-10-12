package net.thechance.mena.wallet.presentation.screen.payment_result

import net.thechance.mena.wallet.presentation.base.BaseViewModel
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class PaymentResultViewModel() : BaseViewModel<PaymentResultScreenState, PaymentResultEffect>(
    PaymentResultScreenState()
), PaymentResultInteractionListener {
    override fun onBackClicked() {
        sendEffect(PaymentResultEffect.NavigateBack)
    }

    override fun onCancelClicked() {
        sendEffect(PaymentResultEffect.NavigateToPreviousScreen)
    }

    override fun onTryAgainClicked() {

    }

    override fun onShowTransactionDetailsClicked() {
        sendEffect(PaymentResultEffect.NavigateToTransactionDetails)
    }
}