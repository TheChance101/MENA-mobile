package net.thechance.mena.admin_panel.presentation.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.admin_panel.presentation.screen.login.component.LoginHeader
import net.thechance.mena.admin_panel.presentation.screen.login.component.PasswordInputField
import net.thechance.mena.admin_panel.presentation.screen.login.component.UsernameInputField
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.login
import net.thechance.mena.admin_panel.resources.login_background
import net.thechance.mena.admin_panel.resources.login_background_img
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel = koinViewModel()) {
    Scaffold {
        val state by viewModel.state.collectAsStateWithLifecycle()
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isPasswordVisible by remember { mutableStateOf(false) }
        Box {
            Image(
                painter = painterResource(Res.drawable.login_background),
                contentDescription = stringResource(Res.string.login_background_img),
                modifier = Modifier.fillMaxSize().align(Alignment.BottomCenter),
                contentScale = ContentScale.FillBounds
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginHeader(modifier = Modifier.padding(top = 64.dp))
                Column(modifier = Modifier.padding(top = 40.dp)) {
                    UsernameInputField(
                        username = state.username,
                        onChangeValue = viewModel::onUsernameChanged
                    )
                    PasswordInputField(
                        modifier = Modifier.padding(top = 24.dp, bottom = 40.dp),
                        password = state.password,
                        isPasswordVisible = state.isPasswordVisible,
                        onVisiblePasswordBtnClicked = viewModel::onVisiblePasswordBtnClicked,
                        onChangeValue = viewModel::onPasswordChanged
                    )
                    PrimaryButton(
                        modifier = Modifier.align(Alignment.End),
                        text = stringResource(Res.string.login),
                        onClick = viewModel::onLoginBtnClicked,
                        isLoading = state.isLoginBtnLoading,
                        isEnabled = state.isLoginBtnEnabled,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 13.dp)
                    )
                }
            }
        }
    }
}