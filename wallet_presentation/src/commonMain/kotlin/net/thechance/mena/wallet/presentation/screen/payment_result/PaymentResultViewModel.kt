@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.payment_result

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.SubmissionStatus
import net.thechance.mena.wallet.presentation.screen.payment_result.args.PaymentResultArgs
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@KoinViewModel
class PaymentResultViewModel(
    @Provided private val transactionRepository: TransactionRepository,
    @Provided private val paymentResultArgs: PaymentResultArgs,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<PaymentResultScreenState, PaymentResultEffect>(
    PaymentResultScreenState()
), PaymentResultInteractionListener {
    private val transactionId = Uuid.parse(paymentResultArgs.transactionId)
    private val submissionStatus =
        SubmissionStatus.valueOf(paymentResultArgs.submitTransactionResultStatus)
    private val receiverName = paymentResultArgs.receiverName
    private val amount = paymentResultArgs.amount
    init {
        updateState {
            it.copy(
                paymentStatus = submissionStatus,
                receiverName = paymentResultArgs.receiverName,
                amount = paymentResultArgs.amount
            )
        }
    }

    override fun onBackClicked() {
        sendEffect(PaymentResultEffect.NavigateBack)
    }

    override fun onTryAgainClicked() {
        if (currentState.tryAgainAttempts >= 3 || currentState.isLoading) return
        updateState { oldState ->
            oldState.copy(
                isLoading = true,
                isTryAgainButtonEnabled = false,
                isCloseButtonEnabled = false
            )
        }
        submitTransaction(transactionId)
    }

    override fun onCloseClicked() {
        sendEffect(PaymentResultEffect.NavigateToPrePaymentScreen)
    }

    override fun onShowTransactionDetailsClicked() {
        sendEffect(PaymentResultEffect.NavigateToTransactionDetails(transactionId))
    }

    private fun submitTransaction(transactionId: Uuid) {
        tryToExecute(
            callee = { transactionRepository.submitTransaction(transactionId) },
            onSuccess = { onSubmitTransactionSuccess() },
            onError = ::onSubmitTransactionFailed,
            dispatcher = dispatcher
        )
    }

    private fun onSubmitTransactionSuccess() {
        updateState {
            it.copy(
                isLoading = false,
                paymentStatus = SubmissionStatus.SUCCESS,
                receiverName = receiverName,
                amount = amount,
                isTryAgainButtonEnabled = false,
                isCloseButtonEnabled = true
            )
        }
    }

    private fun onSubmitTransactionFailed(error: ErrorState) {
        val newAttempts = currentState.tryAgainAttempts + 1
        val canRetry = newAttempts < 3

        updateState {
            it.copy(
                isLoading = false,
                isTryAgainButtonEnabled = canRetry,
                tryAgainAttempts = newAttempts,
                isCloseButtonEnabled = true
            )
        }
        when (error) {
            is ErrorState.NoInternet ->
                updateState { it.copy(paymentStatus = SubmissionStatus.CONNECTION_LOST) }

            else ->
                updateState { it.copy(paymentStatus = SubmissionStatus.UNKNOWN_ERROR) }
        }
    }
}