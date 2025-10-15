@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.payment_result

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.wallet.domain.repository.PaymentRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.SubmissionStatus
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@KoinViewModel
class PaymentResultViewModel(
    @Provided private val paymentRepository: PaymentRepository,
    @Provided private val paymentResultArgs: PaymentResultArgs,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<PaymentResultScreenState, PaymentResultEffect>(
    PaymentResultScreenState()
), PaymentResultInteractionListener {
    private val transactionId = Uuid.parse(paymentResultArgs.transactionId)
    private val submissionStatus = SubmissionStatus.valueOf(paymentResultArgs.submitTransactionResultStatus)

    init {
        updateState { it.copy(paymentStatus = submissionStatus) }
    }

    override fun onBackClicked() {
        sendEffect(PaymentResultEffect.NavigateBack)
    }

    override fun onCancelClicked() {
        sendEffect(PaymentResultEffect.NavigateToScreenBeforePaymentProcess)
    }

    override fun onTryAgainClicked() {
        submitTransaction(transactionId)
    }

    override fun onShowTransactionDetailsClicked() {
        sendEffect(PaymentResultEffect.NavigateToTransactionDetails(transactionId))
    }

    private fun submitTransaction(transactionId: Uuid) {
        tryToExecute(
            callee = {
                paymentRepository.submitTransaction(transactionId)
            },
            onSuccess = { onSubmitTransactionSuccess()},
            onError = ::onSubmitTransactionFailed,
            dispatcher = ioDispatcher
        )
    }

    private fun onSubmitTransactionSuccess() {
        updateState {
            it.copy(
                isLoading = false,
                paymentStatus = SubmissionStatus.SUCCESS
            )
        }
    }

    private fun onSubmitTransactionFailed(error: ErrorState) {
        updateState { it.copy(isLoading = false) }
        when (error) {
            is ErrorState.NoInternet -> updateState { it.copy(SubmissionStatus.CONNECTION_LOST) }
            else -> updateState {
                it.copy(SubmissionStatus.CONNECTION_LOST)
            }
        }
    }
}