package net.thechance.mena.wallet.presentation.screen.confirm_payment

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.confirm_payment_content_failed
import mena.wallet_presentation.generated.resources.confirm_payment_content_success
import net.thechance.mena.wallet.domain.model.TransactionReceiver
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
    private val amount = args.amount

    init {
        getUserBalance()
        getReceiverInfo()
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
        getReceiverInfo()
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

    private fun getReceiverInfo() {
        tryToExecute(
            callee = { transactionRepository.getTransactionReceiver(transactionId) },
            onSuccess = ::onGetReceiverInfoSuccess,
            onError = ::onGetReceiverInfoError,
            onStart = { updateState { it.copy(isGetUserLoading = true) } },
            dispatcher = dispatcher
        )
    }

    private suspend fun onGetUserBalanceSuccess(balance: Double) {
        updateState {
            it.copy(
                isGetBalanceLoading = false,
                paymentUiState = ConfirmPaymentScreenState.PaymentUiState(
                    amount = formatAmount(amount),
                    status = balance >= amount,
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

    private fun onGetUserBalanceError(errorState: ErrorState) {
        updateState { it.copy(isGetBalanceLoading = false, errorState = errorState) }
    }

    private fun onGetReceiverInfoSuccess(transactionReceiverInfo: TransactionReceiver) {
        updateState {
            it.copy(isGetUserLoading = false, receiverUiState = transactionReceiverInfo.toUiState())
        }
    }

    private fun onGetReceiverInfoError(errorState: ErrorState) {
        updateState { it.copy(isGetUserLoading = false, errorState = errorState) }
    }

    private fun onSubmitTransactionSuccess() {
        updateState { it.copy(isPayButtonLoading = false) }
        sendEffect(
            effect = ConfirmPaymentEffect.NavigateToPaymentResultScreen(
                receiverName = state.value.receiverUiState.name,
                amount = amount,
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
                amount = amount,
                transactionId = transactionId,
                submissionStatus = when (error) {
                    ErrorState.NoInternet -> SubmissionStatus.CONNECTION_LOST
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
}