package net.thechance.mena.admin_panel.presentation.screen.login

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import net.thechance.mena.admin_panel.domain.exceptions.InvalidPasswordException
import net.thechance.mena.admin_panel.domain.exceptions.NoInternetException
import net.thechance.mena.admin_panel.domain.exceptions.UnauthorizedException
import net.thechance.mena.admin_panel.domain.use_case.auth.LoginUseCase
import net.thechance.mena.admin_panel.presentation.base.BaseViewModel
import net.thechance.mena.admin_panel.presentation.base.ErrorState
import net.thechance.mena.admin_panel.presentation.model.SnackBarState
import net.thechance.mena.admin_panel.presentation.utils.StringProvider
import net.thechance.mena.admin_panel.presentation.utils.getErrorSnackBarMsg
import net.thechance.mena.admin_panel.presentation.utils.getErrorSnackBarTitle
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class LoginViewModel(
    @Provided
    private val loginUseCase: LoginUseCase,
    @Provided
    private val stringProvider: StringProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<LoginScreenState, LoginEffect>(LoginScreenState()),
    LoginInteractionListener {
    override fun onUsernameChanged(username: String) {
        updateState {
            it.copy(
                username = username.filter { char -> char.isLetterOrDigit() || char == '_' }
            )
        }
    }

    override fun onPasswordChanged(password: String) {
        updateState { it.copy(password = password) }
    }

    override fun onPasswordVisibilityToggled() {
        updateState { it.copy(isPasswordVisible = !state.value.isPasswordVisible) }
    }

    override fun onLoginButtonClicked() {
        tryToExecute(
            callee = ::onLoginClicked,
            onStart = { updateState { it.copy(isLoginButtonLoading = true) } },
            onSuccess = { onLoginSuccess() },
            onError = ::onLoginError,
            onFinish = { updateState { it.copy(isLoginButtonLoading = false) } },
            dispatcher = dispatcher
        )
    }

    private suspend fun onLoginClicked(){
        loginUseCase.login(userName = currentState.username, password = currentState.password)
    }

    private fun onLoginSuccess() {
        sendEffect(LoginEffect.NavigateToAdminPanel)
    }

    private suspend fun onLoginError(errorState: ErrorState) {
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

    override fun mapError(throwable: Throwable): ErrorState {
        return when (throwable) {
            is NoInternetException -> ErrorState.NoInternet
            is InvalidPasswordException -> LoginErrorState.InvalidCredentials
            is UnauthorizedException -> LoginErrorState.InvalidCredentials
            else -> ErrorState.UnknownError
        }
    }
}