package net.thechance.mena.wallet.presentation.screen.confirm_payment

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import net.thechance.mena.wallet.domain.entity.User
import net.thechance.mena.wallet.domain.repository.BalanceRepository
import net.thechance.mena.wallet.domain.repository.UserRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.ErrorState
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
    private val transactionId = args.transactionId
    private val amount = args.amount

    init {
        loadData()
    }

    override fun onBackButtonClicked() {
        sendEffect(ConfirmPaymentEffect.NavigateBack)
    }

    override fun onPayButtonClicked() {
        updateState { it.copy(isPayBtnLoading = true) }
        sendEffect(ConfirmPaymentEffect.NavigateToPaymentResultScreen(transactionId, amount))
    }

    override fun onRefresh() {
        updateState { it.copy(isLoading = true, errorState = null) }
        loadData()
    }

    private fun loadData(){
        viewModelScope.launch {
            onStart()
            listOf(
                async { getUserBalance() },
                async { getReceiverInfo() }
            ).awaitAll()
            onEnd()
        }
    }

    private fun getUserBalance() {
        tryToExecute(
            callee = { balanceRepository.getBalance() },
            onSuccess = ::onGetPaymentConfirmationSuccess,
            onError = ::onError,
            dispatcher = ioDispatcher
        )
    }

    private fun getReceiverInfo() {
        tryToExecute(
            callee = { userRepository.getReceiverByTransactionId(Uuid.parse(transactionId)) },
            onSuccess = ::onGetReceiverInfoSuccess,
            onError = ::onError,
            dispatcher = ioDispatcher
        )
    }

    private fun onGetPaymentConfirmationSuccess(balance: Double) {
        updateState {
            it.copy(
                paymentUiState = ConfirmPaymentScreenState.PaymentUiState(
                    amount = formatAmount(amount),
                    status = balance >= amount,
                    balance = formatAmount(balance)
                )
            )
        }
    }

    private fun onError(errorState: ErrorState) {
        updateState { it.copy(isLoading = false, errorState = errorState) }
    }

    private fun onStart() {
        updateState { it.copy(isLoading = true) }
    }
    private fun onEnd() {
        updateState { it.copy(isLoading = true) }
    }

    private fun onGetReceiverInfoSuccess(userInfo: User) {
        updateState {
            it.copy(receiverUiState = userInfo.toUiState())
        }
    }
}