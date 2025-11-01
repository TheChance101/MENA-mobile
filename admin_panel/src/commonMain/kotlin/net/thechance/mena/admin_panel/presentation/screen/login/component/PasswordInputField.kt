package net.thechance.mena.admin_panel.presentation.screen.login.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.ic_close_eye
import net.thechance.mena.admin_panel.resources.ic_lock
import net.thechance.mena.admin_panel.resources.ic_open_eye
import net.thechance.mena.admin_panel.resources.password
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PasswordInputField(
    password: String,
    isPasswordVisible: Boolean,
    onPasswordVisibilityToggled: () -> Unit,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(bottom = 4.dp),
            text = stringResource(Res.string.password),
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .heightIn(min = 48.dp),
            value = password,
            hint = "",
            onValueChanged = { onValueChange(it) },
            leadingIcon = painterResource(Res.drawable.ic_lock),
            showTrailingDivider = false,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = painterResource(
                if (isPasswordVisible) Res.drawable.ic_open_eye
                else Res.drawable.ic_close_eye
            ),
            onTrailingIconClick = onPasswordVisibilityToggled
        )
    }
}