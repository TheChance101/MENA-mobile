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
    hint: String,
    onValueChanged: (String) -> Unit,
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
        hint = hint,
        onValueChanged = onValueChanged,
        modifier = modifier,
        title = title,
        singleLine = false,
        minLines = minLines,
        maxLines = maxLines,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        errorMessage = errorMessage,
        shape = shape,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        focusRequester = focusRequester,
        onFocusChanged = onFocusChanged,
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
            hint = "Hint",
            modifier = Modifier
        )
    }
}