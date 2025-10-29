package net.thechance.mena.admin_panel.presentation.screen.login

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.presentation.screen.login.component.LoginHeader
import net.thechance.mena.admin_panel.presentation.screen.login.component.PasswordInputField
import net.thechance.mena.admin_panel.presentation.screen.login.component.UsernameInputField
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.login
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen() {
    Scaffold {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isPasswordVisible by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginHeader(modifier = Modifier.padding(top = 64.dp))
            Column(modifier = Modifier.padding(top = 40.dp)) {
                UsernameInputField(
                    username = username,
                    onChangeValue = { username = it }
                )
                PasswordInputField(
                    modifier = Modifier.padding(top = 24.dp, bottom = 40.dp),
                    password = password,
                    isPasswordVisible = isPasswordVisible,
                    onVisiblePasswordBtnClicked = { isPasswordVisible = !isPasswordVisible },
                    onChangeValue = { password = it }
                )
                PrimaryButton(
                    modifier = Modifier.align(Alignment.End),
                    text = stringResource(Res.string.login),
                    onClick = { },
                    isLoading = false,
                    isEnabled = false,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 13.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    MenaTheme {
        LoginScreen()
    }
}