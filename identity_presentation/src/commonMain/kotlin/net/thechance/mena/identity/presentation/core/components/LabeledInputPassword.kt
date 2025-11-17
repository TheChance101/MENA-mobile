package net.thechance.mena.identity.presentation.core.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.ic_close_eye
import mena.identity_presentation.generated.resources.ic_lock
import mena.identity_presentation.generated.resources.ic_open_eye
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun LabeledInputPassword(
    password: String,
    isPasswordVisible: Boolean,
    onChangePassword: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    label: String,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            text = label,
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(bottom = Theme.spacing._4)
        )
        TextField(
            value = password,
            onValueChanged = onChangePassword::invoke,
            hint = "",
            trailingIcon = painterResource(
                if (isPasswordVisible) Res.drawable.ic_close_eye
                else Res.drawable.ic_open_eye
            ),
            leadingIcon = painterResource(Res.drawable.ic_lock),
            showTrailingDivider = false,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            onTrailingIconClick = onTogglePasswordVisibility::invoke,
            isError = errorMessage != null,
            errorMessage = errorMessage,
            maxCharacters = 32

        )
    }
}