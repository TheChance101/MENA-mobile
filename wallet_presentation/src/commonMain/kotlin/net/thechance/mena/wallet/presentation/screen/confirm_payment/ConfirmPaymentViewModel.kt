package net.thechance.mena.wallet.presentation.screen.confirm_payment

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
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
    private val receiverId = args.receiverId
    private val amount = args.amount

    init {
        getPaymentConfirmation()
        getReceiverInfo()
    }

    override fun onBackButtonClicked() {
        sendEffect(ConfirmPaymentEffect.NavigateBack)
    }

    override fun onPayButtonClicked() {
        updateState { it.copy(isPayBtnLoading = true) }
        sendEffect(ConfirmPaymentEffect.NavigateToPaymentResultScreen(receiverId, amount))
    }

    override fun onRefresh() {
        updateState { it.copy(isLoading = true, errorState = null) }
        getPaymentConfirmation()
    }

    private fun getPaymentConfirmation() {
        tryToExecute(
            callee = {
                balanceRepository.getBalance()
            },
            onSuccess = ::onGetPaymentConfirmationSuccess,
            onError = ::onError,
            onStart = ::onStart,
            dispatcher = ioDispatcher
        )
    }

    private fun getReceiverInfo() {
        tryToExecute(
            callee = {
                userRepository.getUserById(Uuid.parse(receiverId))
            },
            onSuccess = ::onGetReceiverInfoSuccess,
            onError = ::onError,
            onStart = ::onStart,
            dispatcher = ioDispatcher
        )
    }

    private fun onGetPaymentConfirmationSuccess(balance: Double) {
        updateState {
            it.copy(
                isLoading = false,
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

    private fun onGetReceiverInfoSuccess(userInfo: User) {
        updateState {
            it.copy(isLoading = false, receiverUiState = userInfo.toUi())
        }
    }
}