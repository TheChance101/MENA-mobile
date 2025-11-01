package net.thechance.mena.admin_panel.presentation.screen.login

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import net.thechance.mena.admin_panel.domain.use_case.LoginUseCase
import net.thechance.mena.admin_panel.presentation.base.BaseViewModel
import net.thechance.mena.admin_panel.presentation.base.ErrorState
import net.thechance.mena.admin_panel.presentation.model.SnackBarState
import net.thechance.mena.admin_panel.presentation.utils.StringProvider
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
        updateState { it.copy(username = username.filter { char -> char.isLetterOrDigit() || char == '_' }) }
    }

    override fun onPasswordChanged(password: String) {
        updateState { it.copy(password = password) }
    }

    override fun onVisiblePasswordBtnClicked() {
        updateState { it.copy(isPasswordVisible = !state.value.isPasswordVisible) }
    }

    override fun onLoginBtnClicked() {
        tryToExecute(
            callee = {
                loginUseCase.login(state.value.username, state.value.password)
            },
            onStart = { updateState { it.copy(isLoginBtnLoading = true) } },
            onSuccess = { onLoginSuccess() },
            onError = ::onLoginError,
            dispatcher = dispatcher
        )
    }

    private fun onLoginSuccess() {
        updateState { it.copy(isLoginBtnLoading = false) }
        sendEffect(LoginEffect.NavigateToAdminPanel)
    }

    private suspend fun onLoginError(errorState: ErrorState) {
        val errorMsg = errorState.getInvalidCredentialsMsg()?.let { stringProvider.getString(it) }
        updateState {
            it.copy(
                isLoginBtnLoading = false,
                usernameErrorMsg = errorMsg,
                passwordErrorMsg = errorMsg,
            )
        }
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
}