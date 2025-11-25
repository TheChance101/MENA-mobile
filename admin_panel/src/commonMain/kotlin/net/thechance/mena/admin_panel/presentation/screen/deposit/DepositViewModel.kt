package net.thechance.mena.admin_panel.presentation.screen.deposit

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import net.thechance.mena.admin_panel.domain.exceptions.InvalidPhoneNumberException
import net.thechance.mena.admin_panel.domain.exceptions.NoInternetException
import net.thechance.mena.admin_panel.domain.model.Country
import net.thechance.mena.admin_panel.domain.repository.depositMoney.DepositMoneyRepository
import net.thechance.mena.admin_panel.domain.use_case.deposit.DepositMoneyUseCase
import net.thechance.mena.admin_panel.presentation.base.BaseViewModel
import net.thechance.mena.admin_panel.presentation.base.ErrorState
import net.thechance.mena.admin_panel.presentation.model.SnackBarState
import net.thechance.mena.admin_panel.presentation.utils.StringProvider
import net.thechance.mena.admin_panel.presentation.utils.formatAmount
import net.thechance.mena.admin_panel.presentation.utils.getErrorSnackBarMsg
import net.thechance.mena.admin_panel.presentation.utils.getErrorSnackBarTitle
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.success_deposit_description
import net.thechance.mena.admin_panel.resources.success_deposit_title

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
): BaseViewModel<DepositScreenState, Unit>(DepositScreenState()), DepositInteractionListener
{
    init {
        getAvailableCountries()
    }

    override fun mapError(throwable: Throwable): ErrorState {
        return when (throwable) {
            is NoInternetException -> ErrorState.NoInternet
            is InvalidPhoneNumberException-> DepositErrorState.NoAccount
            else -> ErrorState.UnknownError
        }
    }

    override fun onFillTheWalletButtonClicked() {
        tryToExecute(
            onStart = { updateState { it.copy(isDepositProcessLoading = true) } },
            callee=::onFillWalletClicked,
            onSuccess = { onDepositSuccess() },
            onError = ::onDepositError,
            dispatcher = dispatcher
        )
    }


    override fun onPhoneNumberChanged(phoneNumber : String) {
        phoneNumber
            .filter { char -> char.isDigit()}
            .let { newPhoneNumber ->
                updateState { it.copy(phoneNumber = newPhoneNumber) }
            }
    }
        override fun onAmountChanged(amount: String) {
            val regex = Regex("^\\d*\\.?\\d*$")
            if (regex.matches(amount)) {
                updateState { it.copy(amount = amount) }
            }
        }

    override fun onRetryClicked() {
        updateState { it.copy(errorState = null) }
        getAvailableCountries()
    }

    override fun onCountryCodeChanged(country: DepositScreenState.CountryUiState) {
        updateState { it.copy(selectedCountry = country) }
    }

    private suspend fun onFillWalletClicked(){
        depositMoneyUseCase.deposit(
            phoneNumber = currentState.phoneNumber,
            amount = currentState.amount.replace(",", "").toDouble(),
            currentState.selectedCountry.toEntity()
        )
    }

    private suspend fun onDepositSuccess(){
        updateState { it.copy(isDepositProcessLoading = false , phoneNumber = "", amount ="") }
        showSnackBar(
            title = stringProvider.getString(Res.string.success_deposit_title),
            message = stringProvider.getString(Res.string.success_deposit_description),
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
        updateState { it.copy(isDepositProcessLoading = false) }
        showSnackBar(
            title = stringProvider.getString(errorState.getErrorSnackBarTitle()),
            message = stringProvider.getString(errorState.getErrorSnackBarMsg()),
            isSuccess = false
        )
    }

    private fun getAvailableCountries() {
        tryToExecute(
            onStart = { updateState { it.copy(isCountriesLoading = true) } },
            callee = {depositMoneyRepository.getCountries()},
            onSuccess = ::onGetCountriesSuccess,
            onError = ::onGetCountriesError,
            onFinish = { updateState { it.copy(isCountriesLoading = false) } },
            dispatcher = dispatcher
        )
    }

    private fun onGetCountriesSuccess(availableCountries: List<Country>) {
        updateState {
            it.copy(
                errorState = null,
                availableCountries = availableCountries.map { it.toUiState() },
                selectedCountry = availableCountries.map{it.toUiState()}.firstOrNull() ?: it.selectedCountry
            )
        }
    }

    private suspend fun onGetCountriesError(errorState: ErrorState) {
        updateState { it.copy(errorState = errorState) }
        showSnackBar(
            title = stringProvider.getString(errorState.getErrorSnackBarTitle()),
            message = stringProvider.getString(errorState.getErrorSnackBarMsg()),
            isSuccess = false
        )

    }
}
