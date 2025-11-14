package net.thechance.mena.designsystem.presentation.component.textField


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.preview.PreviewComponent
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun OtpInputField(
    number: Int?,
    onNumberChanged: (Int?) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(Theme.radius.md),
    containerColor: Color = Theme.colorScheme.background.surfaceLow,
    onFocusChanged: (Boolean) -> Unit = {},
    onPressDeleteWhenEmpty: () -> Unit = {},
    focusRequester: FocusRequester = FocusRequester(),
) {
    val text: TextFieldValue by remember(number) {
        mutableStateOf(
            TextFieldValue(
                text = number?.toString().orEmpty(),
                selection = TextRange(
                    index = if (number != null) 1 else 0
                )
            )
        )
    }

    Box(
        modifier = modifier
            .size(48.dp, 66.dp)
            .background(containerColor, shape),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = text,
            onValueChange = { newText ->
                val newNumber = newText.text
                if (newNumber.length <= 1 && (newNumber.isEmpty() || newNumber.toIntOrNull() != null)) {
                    onNumberChanged(newNumber.toIntOrNull())
                }
            },
            cursorBrush = SolidColor(Theme.colorScheme.primary.primary),
            textStyle = Theme.typography.headline.medium.copy(
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged {
                    onFocusChanged(it.isFocused)
                }
                .onKeyEvent { event ->
                    val isDeleteKeyPressed = event.key == Key.Delete
                    if (isDeleteKeyPressed && number == null) {
                        onPressDeleteWhenEmpty()
                    }
                    false
                },
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .background(Theme.colorScheme.background.surfaceLow, shape)
                        .size(48.dp, 66.dp),
                    contentAlignment = Alignment.Center
                ) {
                    innerTextField()
                }
            }
        )
    }
}


@Preview(showBackground = true,backgroundColor = 0xFFF2F4F7)
@Composable
private fun OtpInputFieldPreview() {
    MenaTheme {
        PreviewComponent(
            title = "OTP"
        ) {
            OtpInputField(
                number = null,
                onNumberChanged = { },
            )
            OtpInputField(
                number = 1,
                onNumberChanged = { },
            )
            OtpInputField(
                number = 2,
                onNumberChanged = { },
            )
        }
    }
}