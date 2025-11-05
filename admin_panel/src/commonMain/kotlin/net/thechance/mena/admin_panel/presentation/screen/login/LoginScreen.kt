package net.thechance.mena.admin_panel.presentation.screen.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.admin_panel.presentation.screen.login.component.LoginHeader
import net.thechance.mena.admin_panel.presentation.screen.login.component.LoginScaffold
import net.thechance.mena.admin_panel.presentation.screen.login.component.PasswordInputField
import net.thechance.mena.admin_panel.presentation.screen.login.component.UsernameInputField
import net.thechance.mena.admin_panel.presentation.utils.ObserveAsEffect
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.login
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import org.jetbrains.compose.resources.stringResource
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
        LoginHeader(modifier = Modifier.padding(top = 64.dp))
        Column(modifier = Modifier.padding(top = 40.dp)) {
            UsernameInputField(
                username = state.username,
                onValueChange = interactionListener::onUsernameChanged
            )
            PasswordInputField(
                modifier = Modifier.padding(top = 24.dp, bottom = 40.dp),
                password = state.password,
                isPasswordVisible = state.isPasswordVisible,
                onPasswordVisibilityToggled = interactionListener::onPasswordVisibilityToggled,
                onValueChange = interactionListener::onPasswordChanged
            )
            PrimaryButton(
                modifier = Modifier
                    .width(70.dp)
                    .align(Alignment.End),
                text = stringResource(Res.string.login),
                onClick = interactionListener::onLoginButtonClicked,
                isLoading = state.isLoginButtonLoading,
                isEnabled = state.isLoginButtonEnabled,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 13.dp)
            )
        }
    }
}

private fun onLoginEffect(effect: LoginEffect) {
    when (effect) {
        LoginEffect.NavigateToAdminPanel -> {}
    }
}