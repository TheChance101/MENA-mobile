package net.thechance.mena.wallet.presentation.screen.wallet

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.balance_fetch_error_description
import mena.wallet_presentation.generated.resources.error
import mena.wallet_presentation.generated.resources.no_internet_title
import net.thechance.mena.wallet.domain.repository.BalanceRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.SnackBarState
import net.thechance.mena.wallet.presentation.utils.StringProvider
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@KoinViewModel
class WalletViewModel(
    @Provided private val stringProvider: StringProvider,
    @Provided private val balanceRepository: BalanceRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<WalletScreenState, WalletEffect>(WalletScreenState()), WalletInteractionListener {

    init {
        getBalance()
    }

    private fun getBalance() {
        tryToExecute(
            onStart = ::onGetBalanceStart,
            onFinish = ::onGetBalanceFinish,
            callee = { balanceRepository.getBalance() },
            onSuccess = ::onGetBalanceSuccess,
            onError = ::onGetBalanceError,
            dispatcher = dispatcher
        )
    }

    private fun onGetBalanceStart() {
        updateState { it.copy(balanceState = it.balanceState.copy(isLoading = true)) }
    }

    private fun onGetBalanceFinish() {
        updateState { it.copy(balanceState = it.balanceState.copy(isLoading = false)) }
    }

    private fun onGetBalanceSuccess(balance: Double) {
        updateState {
            it.copy(
                balanceState = it.balanceState.copy(
                    balance = balance,
                    errorState = null,
                    isLoading = false
                )
            )
        }
    }

    private suspend fun onGetBalanceError(error: ErrorState) {
        updateState {
            it.copy(
                balanceState = it.balanceState.copy(
                    errorState = error,
                    isLoading = false
                )
            )
        }
        val errorMessage = when (error) {
            ErrorState.NoInternet -> Res.string.no_internet_title
            else -> Res.string.balance_fetch_error_description
        }
        showSnackBar(
            title = stringProvider.getString(Res.string.error),
            message = stringProvider.getString(errorMessage),
            isSuccess = false
        )
    }

    private suspend fun showSnackBar(
        title: String,
        message: String,
        isSuccess: Boolean,
        durationMillis: Long = 3000L
    ) {
        updateState { oldState ->
            oldState.copy(
                snackBar = SnackBarState(
                    isVisible = true,
                    title = title,
                    message = message,
                    isSuccess = isSuccess
                )
            )
        }

        delay(durationMillis)

        hideSnackBar()
    }

    private fun hideSnackBar() {
        updateState { oldState ->
            oldState.copy(snackBar = oldState.snackBar.copy(isVisible = false))
        }
    }

    override fun onBackClicked() {
        sendEffect(WalletEffect.NavigateBack)
    }

    override fun onRetryLoadBalanceClicked() {
        getBalance()
    }

    override fun onTransactionHistoryClicked() {
        sendEffect(WalletEffect.NavigateToTransactionHistory)
    }

    override fun onStatementHistoryClicked() {
        sendEffect(WalletEffect.NavigateToStatementHistory)
    }
}