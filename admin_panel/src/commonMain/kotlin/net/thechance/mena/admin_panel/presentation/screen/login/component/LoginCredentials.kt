package net.thechance.mena.admin_panel.presentation.screen.login.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.login
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LoginCredentials(
    username: String,
    password: String,
    isPasswordVisible: Boolean,
    isLoginBtnLoading: Boolean,
    isLoginBtnEnabled: Boolean,
    onLoginBtnClicked: () -> Unit,
    onVisiblePasswordBtnClicked: () -> Unit,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit
) {
    Column(modifier = Modifier.padding(top = 40.dp)) {
        UsernameInputField(
            username = username,
            onChangeValue = onUsernameChanged
        )
        PasswordInputField(
            modifier = Modifier.padding(top = 24.dp, bottom = 40.dp),
            password = password,
            isPasswordVisible = isPasswordVisible,
            onVisiblePasswordBtnClicked = onVisiblePasswordBtnClicked,
            onChangeValue = onPasswordChanged
        )
        PrimaryButton(
            modifier = Modifier
                .width(70.dp)
                .align(Alignment.End),
            text = stringResource(Res.string.login),
            onClick = onLoginBtnClicked,
            isLoading = isLoginBtnLoading,
            isEnabled = isLoginBtnEnabled,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 13.dp)
        )
    }
}