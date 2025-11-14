package net.thechance.mena.designsystem.presentation.component.textField

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import mena.design_system.generated.resources.Res
import mena.design_system.generated.resources.ic_iraq
import mena.design_system.generated.resources.ic_profile
import mena.design_system.generated.resources.silver_tc
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TextField(
    value: String,
    hint: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    title: String? = null,
    leadingIconTint: Color = Theme.colorScheme.shadePrimary,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    showTrailingDivider: Boolean = true,
    errorMessage: String? = null,
    shape: Shape = RoundedCornerShape(Theme.radius.md),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester = FocusRequester(),
    onFocusChanged: (Boolean) -> Unit = {},
    onTrailingIconClick: (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    maxCharacters: Int = Int.MAX_VALUE,
) {
    BasicTextField(
        value = value,
        hint = hint,
        onValueChanged = onValueChanged,
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        title = title,
        leadingIconTint = leadingIconTint,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        showTrailingDivider = showTrailingDivider,
        errorMessage = errorMessage,
        shape = shape,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        focusRequester = focusRequester,
        onFocusChanged = onFocusChanged,
        onTrailingIconClick = onTrailingIconClick,
        visualTransformation = visualTransformation,
        maxCharacters = maxCharacters
    )
}
@Preview(showBackground = true, backgroundColor = 0xFFF2F4F7)
@Composable
private fun PreviewTextFieldComponent() {
    MenaTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            net.thechance.mena.designsystem.presentation.component.text.Text(
                text = "Text field",
                style = Theme.typography.headline.small,
                color = Theme.colorScheme.shadeTertiary
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = "adad",
                    onValueChanged = {},
                    hint = "Placeholder",
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = painterResource(Res.drawable.ic_profile)
                )

                TextField(
                    value = "",
                    onValueChanged = {},
                    hint = "Placeholder",
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = painterResource(Res.drawable.ic_profile),
                    trailingIcon = painterResource(Res.drawable.silver_tc)
                )

                TextField(
                    value = "",
                    onValueChanged = {},
                    hint = "Placeholder",
                    leadingIcon = painterResource(Res.drawable.ic_profile),
                    isError = true,
                    errorMessage = "error message",
                    modifier = Modifier.fillMaxWidth(),
                )

                MultiLineTextField(
                    value = "",
                    onValueChanged = {},
                    hint = "Placeholder",
                    modifier = Modifier.fillMaxWidth()
                )

                MobileNumberTextField(
                    value = "",
                    onValueChanged = { },
                    title = "title",
                    hint = "value",
                    leadingIcon = painterResource(Res.drawable.ic_profile),
                    leadingContent = {
                        MobileNumberLeadingContent(
                            countryCode = "+964",
                            countryPainter = painterResource(Res.drawable.ic_iraq),
                            onClick = {}
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}