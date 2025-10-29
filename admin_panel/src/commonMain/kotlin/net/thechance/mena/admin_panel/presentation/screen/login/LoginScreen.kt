package net.thechance.mena.admin_panel.presentation.screen.login

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.thechance.mena.admin_panel.presentation.component.AdminPanelScaffold
import net.thechance.mena.admin_panel.presentation.screen.login.component.PasswordInputField
import net.thechance.mena.admin_panel.presentation.screen.login.component.UsernameInputField
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme

@Composable
fun LoginScreen() {
    AdminPanelScaffold {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isPasswordVisible by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UsernameInputField(
                username = username,
                onChangeValue = { username = it }
            )
            PasswordInputField(
                password = password,
                isPasswordVisible = isPasswordVisible,
                onVisiblePasswordBtnClicked = { isPasswordVisible = !isPasswordVisible },
                onChangeValue = { password = it }
            )
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