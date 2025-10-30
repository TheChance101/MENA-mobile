package net.thechance.mena.admin_panel.presentation.screen.login.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.ic_user
import net.thechance.mena.admin_panel.resources.username
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun UsernameInputField(
    username: String,
    usernameErrorMsg: String?,
    onChangeValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(bottom = Theme.spacing._4),
            text = stringResource(Res.string.username),
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(48.dp),
            value = username,
            hint = "",
            onValueChanged = { onChangeValue(it) },
            leadingIcon = painterResource(Res.drawable.ic_user),
            showTrailingDivider = false,
            visualTransformation = visualTransformation,
            isError = usernameErrorMsg != null,
            errorMessage = usernameErrorMsg
        )
    }
}