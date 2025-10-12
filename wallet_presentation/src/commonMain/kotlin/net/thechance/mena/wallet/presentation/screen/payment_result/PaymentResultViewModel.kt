@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.payment_result

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.wallet.domain.repository.PaymentRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.SubmitTransactionResultStatus
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@KoinViewModel
class PaymentResultViewModel(
    @Provided private val paymentRepository: PaymentRepository,
    @Provided private val transactionId: Uuid,
    @Provided private val submitTransactionResultStatus: SubmitTransactionResultStatus,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<PaymentResultScreenState, PaymentResultEffect>(
    PaymentResultScreenState()
), PaymentResultInteractionListener {

    init {
        updateState { it.copy(submitTransactionResultStatus) }
    }

    private fun submitTransaction(transactionId: Uuid) {
        tryToExecute(
            callee = {
                paymentRepository.submitTransaction(transactionId)
            },
            onSuccess = ::onSubmitTransactionSuccess,
            onError = ::onSubmitTransactionFailed,
            dispatcher = ioDispatcher
        )
    }

    override fun onBackClicked() {
        sendEffect(PaymentResultEffect.NavigateBack)
    }

    override fun onCancelClicked() {
        sendEffect(PaymentResultEffect.NavigateToPreviousScreen)
    }

    override fun onTryAgainClicked() {
        submitTransaction(transactionId)
    }

    override fun onShowTransactionDetailsClicked() {
        sendEffect(PaymentResultEffect.NavigateToTransactionDetails)
    }

    private fun onSubmitTransactionSuccess(unit: Unit) {
        updateState {
            it.copy(
                isLoading = false,
                paymentStatus = SubmitTransactionResultStatus.SUCCESS
            )
        }
    }

    private fun onSubmitTransactionFailed(error: ErrorState) {
        updateState { it.copy(isLoading = false) }
        when (error) {
            is ErrorState.NoInternet -> updateState { it.copy(SubmitTransactionResultStatus.CONNECTION_LOST) }
            else -> updateState {
                it.copy(SubmitTransactionResultStatus.CONNECTION_LOST)
            }
        }
    }
}