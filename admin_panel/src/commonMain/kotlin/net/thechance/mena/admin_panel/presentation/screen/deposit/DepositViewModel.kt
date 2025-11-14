package net.thechance.mena.admin_panel.presentation.screen.deposit

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import net.thechance.mena.admin_panel.domain.repository.depositMoney.DepositMoneyRepository
import net.thechance.mena.admin_panel.domain.use_case.deposit.DepositMoneyUseCase
import net.thechance.mena.admin_panel.presentation.base.BaseViewModel
import net.thechance.mena.admin_panel.presentation.base.ErrorState
import net.thechance.mena.admin_panel.presentation.model.SnackBarState
import net.thechance.mena.admin_panel.presentation.screen.deposit.mapper.toEntity
import net.thechance.mena.admin_panel.presentation.utils.StringProvider
import net.thechance.mena.admin_panel.presentation.utils.getErrorSnackBarMsg
import net.thechance.mena.admin_panel.presentation.utils.getErrorSnackBarTitle

import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class DepositViewModel (
    @Provided
    private val depositMoneyUseCase : DepositMoneyUseCase,
    @Provided
    private val stringProvider: StringProvider,
    @Provided
    private val depositMoneyRepository: DepositMoneyRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
): BaseViewModel<DepositScreenState, DepositEffect>(DepositScreenState()), DepositInteractionListener
{
    override fun mapError(throwable: Throwable): ErrorState {
        TODO("Not yet implemented")
    }

    override fun onFillTheWalletButtonClicked() {
        tryToExecute(
            callee=::onFillWalletButtonClicked,
            onSuccess = { onDepositSuccess() },
            onError = ::onDepositError,
            dispatcher = dispatcher
        )
    }

    override fun onPhoneNumberChanged(phoneNumber : String) {
        updateState { it.copy(phoneNumber = phoneNumber) }
    }

    override fun onAmountChanged(amount : Double) {
        updateState { it.copy(amount = amount) }
    }

    override fun onCountryCodeSelected() {
        updateState { it.copy(isCountryBottomSheetVisible = true) }
    }

    override fun onCountryCodeChanged(country: DepositScreenState.CountryUiState) {
        updateState { it.copy(country = country) }
        onCountryBottomSheetDismissed()
    }

    override fun onCountryBottomSheetDismissed() {
        updateState { it.copy(isCountryBottomSheetVisible = false) }
    }

    private suspend fun onFillWalletButtonClicked(){
        depositMoneyUseCase.deposit(phoneNumber = currentState.phoneNumber ,amount =currentState.amount , currentState.country.toEntity())
    }
    private suspend fun onDepositSuccess(){
        showSnackBar(
            title = "success",
            message = "yes",
            isSuccess = true
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
    private suspend fun onDepositError(errorState: ErrorState) {
        showSnackBar(
            title = stringProvider.getString(errorState.getErrorSnackBarTitle()),
            message = stringProvider.getString(errorState.getErrorSnackBarMsg()),
            isSuccess = false
        )
    }



}