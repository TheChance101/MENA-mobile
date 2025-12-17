package net.thechance.mena.admin_panel.presentation.screen.mainContainer

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import net.thechance.mena.admin_panel.domain.exceptions.NoInternetException
import net.thechance.mena.admin_panel.domain.repository.authentication.AdminAuthenticationRepository
import net.thechance.mena.admin_panel.presentation.base.BaseViewModel
import net.thechance.mena.admin_panel.presentation.base.ErrorState
import net.thechance.mena.admin_panel.presentation.model.SnackBarState
import net.thechance.mena.admin_panel.presentation.utils.StringProvider
import net.thechance.mena.admin_panel.presentation.utils.getErrorSnackBarMsg
import net.thechance.mena.admin_panel.presentation.utils.getErrorSnackBarTitle
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class MainContainerViewmodel(
    @Provided
    private val authenticationRepository: AdminAuthenticationRepository,
    @Provided
    private val stringProvider: StringProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    BaseViewModel<MainContainerScreenState, MainContainerEffect>(
        MainContainerScreenState()
    ), MainContainerInteractionListener {
    init {
        checkAuthenticationStatus()
    }

    override fun onTabSelected(tab: MainContainerScreenState.SelectedSidebarTab) {
        updateState { it.copy(selectedSidebarTab = tab) }
        sendEffect(determineEffect(tab))
    }

    override fun onLogOutClicked() {
        updateState { it.copy(isLogOutDialogShown = true) }
    }

    override fun onLogoutDismissed() {
        updateState { it.copy(isLogOutDialogShown = false) }
    }

    override fun onLogoutConfirmed() {
        tryToExecute(
            callee = { authenticationRepository.logout() },
            onSuccess = { onSuccessLoggedOut() },
            onError = ::onFailureLoggedOut,
            dispatcher = dispatcher
        )

    }

    override fun mapError(throwable: Throwable): ErrorState {
        return when (throwable) {
            is NoInternetException -> ErrorState.NoInternet
            else -> ErrorState.UnknownError
        }
    }

    private fun onSuccessLoggedOut() {
        updateState {
            it.copy(
                isLogOutDialogShown = false,
                selectedSidebarTab = MainContainerScreenState.SelectedSidebarTab.DUKAN_MANAGEMENT
            )
        }
        sendEffect(MainContainerEffect.NavigateToLogInScreen)
    }

    private suspend fun onFailureLoggedOut(errorState: ErrorState) {
        updateState { it.copy(isLogOutDialogShown = false) }
        showSnackBar(
            title = stringProvider.getString(errorState.getErrorSnackBarTitle()),
            message = stringProvider.getString(errorState.getErrorSnackBarMsg()),
            isSuccess = false
        )
    }

    private fun checkAuthenticationStatus() {
        tryToExecute(
            callee = authenticationRepository::isUserLoggedIn,
            onSuccess = ::onSuccessCheckedAuthentication,
            onError = ::onFailureCheckedAuthentication,
            dispatcher = dispatcher
        )
    }

    private suspend fun onSuccessCheckedAuthentication(token: Flow<Boolean>) {
        token.collect { isUserLoggedIn ->
            updateState { it.copy(authenticationStatus = isUserLoggedIn) }
            if (isUserLoggedIn) {
                sendEffect(MainContainerEffect.NavigateToAdminPanelScreen)
            } else {
                sendEffect(MainContainerEffect.NavigateToLogInScreen)
            }
        }
    }

    private suspend fun onFailureCheckedAuthentication(errorState: ErrorState) {
        showSnackBar(
            title = stringProvider.getString(errorState.getErrorSnackBarTitle()),
            message = stringProvider.getString(errorState.getErrorSnackBarMsg()),
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

    private fun determineEffect(selectedSidebarTab: MainContainerScreenState.SelectedSidebarTab): MainContainerEffect =
        when (selectedSidebarTab) {
            MainContainerScreenState.SelectedSidebarTab.USERS_MANAGEMENT ->
                MainContainerEffect.NavigateToUsersManagementScreen

            MainContainerScreenState.SelectedSidebarTab.DUKAN_MANAGEMENT ->
                MainContainerEffect.NavigateToDukanManagementScreen

            MainContainerScreenState.SelectedSidebarTab.DUKAN_REQUEST ->
                MainContainerEffect.NavigateToDukanRequestsScreen

            MainContainerScreenState.SelectedSidebarTab.DEPOSIT ->
                MainContainerEffect.NavigateToDepositScreen
        }
}