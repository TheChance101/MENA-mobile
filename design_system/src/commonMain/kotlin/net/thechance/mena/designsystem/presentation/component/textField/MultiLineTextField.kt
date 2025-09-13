package net.thechance.mena.designsystem.presentation.component.textField

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Shape
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MultiLineTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    minLines: Int = 5,
    maxLines: Int = 5,
    title: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    shape: Shape = RoundedCornerShape(Theme.radius.md),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester = FocusRequester(),
    onFocusChanged: (Boolean) -> Unit = {},
) {
    BasicTextField(
        value = value,
        onValueChanged = onValueChanged,
        placeholder = placeholder,
        leadingIcon = null,
        trailingIcon = null,
        title = title,
        enabled = enabled,
        minLines = minLines,
        maxLines = maxLines,
        singleLine = false,
        readOnly = readOnly,
        isError = isError,
        errorMessage = errorMessage,
        shape = shape,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        focusRequester = focusRequester,
        onFocusChanged = onFocusChanged,
        modifier = modifier
    )
}

@Preview
@Composable
fun PreviewMultilineTextField() {
    MenaTheme {
        val (value, onValueChanged) = remember {
            mutableStateOf("")
        }

        MultiLineTextField(
            value = value,
            onValueChanged = onValueChanged,
            placeholder = "Placeholder",
            modifier = Modifier
        )
    }
}