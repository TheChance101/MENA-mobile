package net.thechance.mena.wallet.presentation.screen.confirm_payment

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.wallet.domain.entity.User
import net.thechance.mena.wallet.domain.repository.BalanceRepository
import net.thechance.mena.wallet.domain.repository.UserRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.SubmitTransactionResultStatus
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
    @Provided private val userRepository: UserRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
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
        updateState { it.copy(isPayBtnLoading = true) }
        sendEffect(
            ConfirmPaymentEffect.NavigateToPaymentResultScreen(
                receiverName = state.value.receiverUiState.name,
                amount = amount,
                transactionId = transactionId,
                submitTransactionResultStatus = SubmitTransactionResultStatus.SUCCESS
            )
        )
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
            dispatcher = ioDispatcher
        )
    }

    private fun getReceiverInfo() {
        tryToExecute(
            callee = { userRepository.getReceiverByTransactionId(transactionId) },
            onSuccess = ::onGetReceiverInfoSuccess,
            onError = ::onGetReceiverInfoError,
            onStart = { updateState { it.copy(isGetUserLoading = true) } },
            dispatcher = ioDispatcher
        )
    }

    private fun onGetUserBalanceSuccess(balance: Double) {
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
    }

    private fun onGetUserBalanceError(errorState: ErrorState) {
        updateState { it.copy(isGetBalanceLoading = false, errorState = errorState) }
    }

    private fun onGetReceiverInfoSuccess(userInfo: User) {
        updateState {
            it.copy(isGetUserLoading = false, receiverUiState = userInfo.toUiState())
        }
    }

    private fun onGetReceiverInfoError(errorState: ErrorState) {
        updateState { it.copy(isGetUserLoading = false, errorState = errorState) }
    }
}