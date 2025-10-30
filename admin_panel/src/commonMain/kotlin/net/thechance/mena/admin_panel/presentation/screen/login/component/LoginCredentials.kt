package net.thechance.mena.admin_panel.presentation.screen.login.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.login
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LoginCredentials(
    username: String,
    password: String,
    usernameErrorMsg: String?,
    passwordErrorMsg: String?,
    isPasswordVisible: Boolean,
    isLoginBtnLoading: Boolean,
    isLoginBtnEnabled: Boolean,
    onLoginBtnClicked: () -> Unit,
    onVisiblePasswordBtnClicked: () -> Unit,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(top = 40.dp)) {
        UsernameInputField(
            username = username,
            usernameErrorMsg = usernameErrorMsg,
            onChangeValue = onUsernameChanged,
            visualTransformation = if (username.isNotEmpty()) AtPrefixTransformation
            else VisualTransformation.None
        )
        PasswordInputField(
            modifier = Modifier.padding(top = 24.dp, bottom = 40.dp),
            password = password,
            passwordErrorMsg = passwordErrorMsg,
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

private object AtPrefixTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val transformedText = AnnotatedString("@" + text.text)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = offset + 1
            override fun transformedToOriginal(offset: Int): Int = (offset - 1).coerceAtLeast(0)
        }

        return TransformedText(transformedText, offsetMapping)
    }
}