package net.thechance.mena.wallet.presentation.screen.confirm_payment

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.confirm_payment_content_failed
import mena.wallet_presentation.generated.resources.confirm_payment_content_success
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.repository.BalanceRepository
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.SubmissionStatus
import net.thechance.mena.wallet.presentation.screen.confirm_payment.args.ConfirmPaymentArgs
import net.thechance.mena.wallet.presentation.utils.StringProvider
import net.thechance.mena.wallet.presentation.utils.formatAmount
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@KoinViewModel
class ConfirmPaymentViewModel(
    @Provided private val args: ConfirmPaymentArgs,
    @Provided private val balanceRepository: BalanceRepository,
    @Provided private val transactionRepository: TransactionRepository,
    @Provided private val stringProvider: StringProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<ConfirmPaymentScreenState, ConfirmPaymentEffect>(
    ConfirmPaymentScreenState()
), ConfirmPaymentInteractionListener {
    private val transactionId = Uuid.parse(args.transactionId)

    init {
        getUserBalance()
    }

    override fun onBackButtonClicked() {
        sendEffect(ConfirmPaymentEffect.NavigateBack)
    }

    override fun onPayButtonClicked() {
        submitTransaction(transactionId)
    }

    override fun onRefresh() {
        updateState { it.copy(errorState = null) }
        getUserBalance()
    }

    private fun onSubmitTransactionSuccess() {
        updateState { it.copy(isPayButtonLoading = false) }
        sendEffect(
            effect = ConfirmPaymentEffect.NavigateToPaymentResultScreen(
                receiverName = state.value.receiverUiState.name,
                amount = currentState.amount,
                transactionId = transactionId,
                submissionStatus = SubmissionStatus.SUCCESS
            )
        )
    }

    private fun onSubmitTransactionFailed(error: ErrorState) {
        updateState { it.copy(isPayButtonLoading = false) }
        sendEffect(
            effect = ConfirmPaymentEffect.NavigateToPaymentResultScreen(
                receiverName = state.value.receiverUiState.name,
                amount = currentState.amount,
                transactionId = transactionId,
                submissionStatus = when (error) {
                    ErrorState.NoInternet -> SubmissionStatus.CONNECTION_LOST
                    ErrorState.BlockedReceiver -> SubmissionStatus.BLOCKED_RECEIVER
                    else -> SubmissionStatus.UNKNOWN_ERROR
                }
            )
        )
    }

    private fun submitTransaction(transactionId: Uuid) {
        tryToExecute(
            callee = { transactionRepository.submitTransaction(transactionId) },
            onStart = { updateState { it.copy(isPayButtonLoading = true) } },
            onSuccess = { onSubmitTransactionSuccess() },
            onError = ::onSubmitTransactionFailed,
            dispatcher = dispatcher
        )
    }

    private fun getUserBalance() {
        tryToExecute(
            callee = { balanceRepository.getBalance() },
            onSuccess = ::onGetUserBalanceSuccess,
            onError = ::onGetUserBalanceError,
            onStart = { updateState { it.copy(isGetBalanceLoading = true) } },
            dispatcher = dispatcher
        )
    }

    private fun onGetUserBalanceSuccess(balance: Double) {
        getTransactionDetails(transactionId, balance)
    }

    private fun onGetUserBalanceError(errorState: ErrorState) {
        updateState { it.copy(isGetBalanceLoading = false, errorState = errorState) }
    }

    private fun getTransactionDetails(transactionId: Uuid, balance: Double) {
        tryToExecute(
            onStart = ::onGetTransactionDetailsStart,
            callee = { transactionRepository.getTransactionById(transactionId) },
            onSuccess = { transaction -> onGetTransactionDetailsSuccess(transaction, balance) },
            onError = ::onGetTransactionDetailsError

        )
    }

    private fun onGetTransactionDetailsStart() {
        updateState { it.copy(isGetTransactionDetailsLoading = true) }
    }

    private suspend fun onGetTransactionDetailsSuccess(transaction: Transaction, balance: Double) {
        updateState {
            it.copy(
                isGetTransactionDetailsLoading = false,
                isGetBalanceLoading = false,
                amount = transaction.amount,
                receiverUiState = transaction.toReceiverInfoUiState(),
                paymentUiState = ConfirmPaymentScreenState.PaymentUiState(
                    amount = formatAmount(transaction.amount),
                    status = balance >= transaction.amount,
                    balance = formatAmount(balance)
                )
            )
        }
        updateUserMessage()
    }

    private suspend fun updateUserMessage() {
        val userMessage = if (state.value.paymentUiState.status) {
            stringProvider.getString(
                Res.string.confirm_payment_content_success,
                state.value.paymentUiState.balance
            )
        } else {
            stringProvider.getString(
                Res.string.confirm_payment_content_failed,
                state.value.paymentUiState.balance
            )
        }
        updateState {
            it.copy(userMessage = userMessage)
        }
    }

    private fun onGetTransactionDetailsError(errorState: ErrorState) {
        updateState { it.copy(isGetTransactionDetailsLoading = false, errorState = errorState) }
    }
}