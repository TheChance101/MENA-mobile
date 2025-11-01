package net.thechance.mena.admin_panel.presentation.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.admin_panel.presentation.component.SnackBarContainer
import net.thechance.mena.admin_panel.presentation.screen.login.component.LoginCredentials
import net.thechance.mena.admin_panel.presentation.screen.login.component.LoginHeader
import net.thechance.mena.admin_panel.presentation.utils.ObserveAsEffect
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.login_background
import net.thechance.mena.admin_panel.resources.login_background_img
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
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
    Scaffold {
        Box(
            modifier = Modifier.background(
                Brush.linearGradient(
                    listOf(
                        Theme.colorScheme.background.surface,
                        Theme.colorScheme.background.surface,
                        Theme.colorScheme.background.surface.copy(alpha = 0.6f),
                        Theme.colorScheme.background.surface.copy(alpha = 0.2f),
                    )
                )
            )
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomCenter),
                painter = painterResource(Res.drawable.login_background),
                contentDescription = stringResource(Res.string.login_background_img),
                contentScale = ContentScale.FillBounds
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 68.dp, top = 16.dp)
            ) { SnackBarContainer(state.snackBar) }
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
}

private fun onLoginEffect(effect: LoginEffect) {
    when (effect) {
        LoginEffect.NavigateToAdminPanel -> {}
    }
}