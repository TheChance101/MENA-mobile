package net.thechance.mena.wallet.presentation.screen.wallet

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.balance_fetch_error_description
import mena.wallet_presentation.generated.resources.error
import net.thechance.mena.wallet.domain.repository.BalanceRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.SnackBarState
import net.thechance.mena.wallet.presentation.base.UiState
import org.jetbrains.compose.resources.StringResource
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class WalletViewModel(
    @Provided private val balanceRepository: BalanceRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<WalletScreenState, WalletEffect>(WalletScreenState()), WalletInteractionListener {

    init {
        getBalance()
    }

    private fun getBalance() {
        tryToExecute(
            onStart = ::onGetBalanceStart,
            callee = { balanceRepository.getBalance() },
            onSuccess = ::onGetBalanceSuccess,
            onError = ::onGetBalanceError,
            dispatcher = ioDispatcher
        )
    }

    private fun onGetBalanceStart() {
        updateState { it.copy(balance = UiState.Loading) }
    }

    private fun onGetBalanceSuccess(balance: Double) {
        updateState { it.copy(balance = UiState.Success(balance)) }
    }

    private suspend fun onGetBalanceError(throwable: Throwable) {
        updateState { it.copy(balance = UiState.Error(throwable)) }

        showSnackBar(
            titleRes = Res.string.error,
            messageRes = Res.string.balance_fetch_error_description,
            isSuccess = false
        )
    }


    private suspend fun showSnackBar(
        titleRes: StringResource,
        messageRes: StringResource,
        isSuccess: Boolean,
        durationMillis: Long = 3000L
    ) {
        updateState { oldState ->
            oldState.copy(
                snackBar = SnackBarState(
                    isVisible = true,
                    titleRes = titleRes,
                    messageRes = messageRes,
                    isSuccess = isSuccess
                )
            )
        }

        delay(durationMillis)

        hideSnackBar()
    }

    private fun hideSnackBar() {
        updateState { oldState ->
            oldState.copy(
                snackBar = oldState.snackBar.copy(isVisible = false)
            )
        }
    }

    override fun onBackClicked() {
        sendEffect(WalletEffect.NavigateBack)
    }

    override fun onRetryLoadBalanceClicked() {
        getBalance()
    }
}