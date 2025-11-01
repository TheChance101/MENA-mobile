package net.thechance.mena.admin_panel.presentation.screen.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.admin_panel.presentation.screen.login.component.LoginCredentials
import net.thechance.mena.admin_panel.presentation.screen.login.component.LoginHeader
import net.thechance.mena.admin_panel.presentation.screen.login.component.LoginScaffold
import net.thechance.mena.admin_panel.presentation.utils.ObserveAsEffect
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun LoginScreen(viewModel: LoginViewModel = koinViewModel()) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect -> onLoginEffect(effect) }
    )

    LoginScreenContent(state = state, interactionListener = viewModel)
}

@Composable
private fun LoginScreenContent(
    state: LoginScreenState,
    interactionListener: LoginInteractionListener
) {
    LoginScaffold(snackBarState = state.snackBar){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = Theme.spacing._16)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginHeader(modifier = Modifier.padding(top = 64.dp))

            LoginCredentials(
                username = state.username,
                password = state.password,
                usernameErrorMsg = state.usernameErrorMsg,
                passwordErrorMsg = state.passwordErrorMsg,
                isPasswordVisible = state.isPasswordVisible,
                isLoginBtnLoading = state.isLoginBtnLoading,
                isLoginBtnEnabled = state.isLoginBtnEnabled,
                onLoginBtnClicked = interactionListener::onLoginBtnClicked,
                onVisiblePasswordBtnClicked = interactionListener::onVisiblePasswordBtnClicked,
                onUsernameChanged = interactionListener::onUsernameChanged,
                onPasswordChanged = interactionListener::onPasswordChanged
            )
        }
    }
}

private fun onLoginEffect(effect: LoginEffect) {
    when (effect) {
        LoginEffect.NavigateToAdminPanel -> {}
    }
}