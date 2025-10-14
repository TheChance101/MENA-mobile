package net.thechance.mena.wallet.presentation.screen.confirm_payment

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.wallet.domain.model.PaymentConfirmation
import net.thechance.mena.wallet.domain.repository.PaymentRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.SubmitTransactionResultStatus
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@KoinViewModel
class ConfirmPaymentViewModel(
    @Provided private val args: ConfirmPaymentArgs,
    @Provided private val paymentRepository: PaymentRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<ConfirmPaymentScreenState, ConfirmPaymentEffect>(
    ConfirmPaymentScreenState()
), ConfirmPaymentInteractionListener {
    private val receiverId = args.receiverId
    private val amount = args.amount

    init {
        getPaymentConfirmation()
    }

    private fun getPaymentConfirmation() {
        tryToExecute(
            callee = {
                paymentRepository.getPaymentConfirmation(
                    receiverId = Uuid.parse(receiverId),
                    amount = amount
                )
            },
            onSuccess = ::onGetPaymentConfirmationSuccess,
            onError = ::onGetPaymentConfirmationError,
            onStart = ::onGetPaymentConfirmationStart,
            dispatcher = ioDispatcher
        )
    }

    override fun onBackButtonClicked() {
        sendEffect(ConfirmPaymentEffect.NavigateBack)
    }

    override fun onPayButtonClicked() {
        updateState { it.copy(isPayBtnLoading = true) }
        submitTransaction(dummyTransactionId)
    }

    override fun onRefresh() {
        updateState { it.copy(isLoading = true, errorState = null) }
        getPaymentConfirmation()
    }

    private fun onGetPaymentConfirmationSuccess(paymentConfirmation: PaymentConfirmation) {
        updateState {
            it.copy(isLoading = false, paymentUiState = paymentConfirmation.toUi(amount))
        }
    }

    private fun onGetPaymentConfirmationError(errorState: ErrorState) {
        updateState { it.copy(isLoading = false, errorState = errorState) }
    }

    private fun onGetPaymentConfirmationStart() {
        updateState { it.copy(isLoading = true) }
    }

    private fun onSubmitTransactionSuccess() {
        updateState { it.copy(isPayBtnLoading = false) }
        sendEffect(
            effect = ConfirmPaymentEffect.NavigateToPaymentResultScreen(
                receiverId,
                amount,
                dummyTransactionId,
                SubmitTransactionResultStatus.SUCCESS
            )
        )
    }

    private fun onSubmitTransactionFailed(error: ErrorState) {
        updateState { it.copy(isLoading = false) }
        sendEffect(
            effect = ConfirmPaymentEffect.NavigateToPaymentResultScreen(
                receiverId,
                amount,
                dummyTransactionId,
                submitTransactionResultStatus = when (error) {
                    ErrorState.NoInternet -> SubmitTransactionResultStatus.CONNECTION_LOST
                    else -> SubmitTransactionResultStatus.UNKNOWN_ERROR
                }
            )
        )
    }

    private fun submitTransaction(transactionId: Uuid) {
        tryToExecute(
            callee = {
                paymentRepository.submitTransaction(transactionId)
            },
            onSuccess = { onSubmitTransactionSuccess() },
            onError = ::onSubmitTransactionFailed,
            dispatcher = ioDispatcher
        )
    }

    private val dummyTransactionId = Uuid.parse("123e4567-e89b-12d3-a456-426614174000")
}