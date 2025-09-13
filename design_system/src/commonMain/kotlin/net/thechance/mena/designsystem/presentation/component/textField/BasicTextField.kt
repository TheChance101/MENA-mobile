package net.thechance.mena.designsystem.presentation.component.textField

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mena.design_system.generated.resources.Res
import mena.design_system.generated.resources.ic_user
import mena.design_system.generated.resources.silver_tc
import net.thechance.mena.designsystem.presentation.component.icon.MenaIcon
import net.thechance.mena.designsystem.presentation.component.image.MenaImage
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BasicTextField(
    value: String,
    placeholder: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    leadingContent: @Composable (() -> Unit)? = null,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    title: String? = null,
    leadingIconTint: Color = Theme.colorScheme.shadePrimary,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    shape: Shape = RoundedCornerShape(Theme.radius.md),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester = FocusRequester(),
    onFocusChanged: (Boolean) -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column(modifier) {
        title?.let {
            MenaText(
                text = title,
                color = Theme.colorScheme.shadePrimary,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                letterSpacing = 0.sp,
                style = Theme.typography.title.small,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Row(Modifier.fillMaxWidth()) {
            leadingContent?.let {
                leadingContent()
                Spacer(Modifier.width(Theme.spacing._4))
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChanged,
                enabled = enabled,
                readOnly = readOnly,
                minLines = minLines,
                maxLines = if (singleLine) 1 else maxLines,
                textStyle = Theme.typography.body.small,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                cursorBrush = SolidColor(Theme.colorScheme.primary.primary),
                decorationBox = { innerTextField ->
                    TextFieldContent(
                        innerTextField = innerTextField,
                        text = value,
                        isError = isError,
                        singleLine = singleLine,
                        placeholder = placeholder,
                        leadingIcon = leadingIcon,
                        trailingIcon = trailingIcon,
                        leadingIconTint = leadingIconTint
                    )
                },
                visualTransformation = visualTransformation,
                modifier = Modifier
                    .weight(1f)
                    .clip(shape)
                    .background(color = Theme.colorScheme.background.surfaceLow)
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        onFocusChanged(it.isFocused)
                    }
            )
        }

        errorMessage?.let {
            MenaText(
                text = errorMessage,
                color = Theme.colorScheme.error,
                style = Theme.typography.label.small,
                modifier = Modifier.padding(
                    start = 12.dp,
                    top = 2.dp
                )
            )
        }
    }
}

@Composable
private fun TextFieldContent(
    innerTextField: @Composable () -> Unit,
    text: String,
    placeholder: String,
    leadingIcon: Painter?,
    trailingIcon: Painter?,
    leadingIconTint: Color,
    isError: Boolean,
    singleLine: Boolean
) {
    Row(
        modifier = Modifier.padding(12.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon?.let {
            //todo add error color
            val errorTintColor = Brush.linearGradient(
                listOf(
                    Theme.colorScheme.border.error,
                    Color(0xFFEA8E87)
                )
            )

            MenaIcon(
                painter = leadingIcon,
                tint = if (isError) Theme.colorScheme.border.error else leadingIconTint,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = Theme.spacing._8)
                    .size(24.dp)
            )
        }

        InnerTextFieldWithPlaceHolder(
            innerTextField = innerTextField,
            text = text,
            placeholder = placeholder,
            singleLine = singleLine,
            modifier = Modifier.weight(1f)
        )

        trailingIcon?.let {
            VerticalDivider()
            MenaImage(
                painter = trailingIcon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }

}

@Composable
private fun InnerTextFieldWithPlaceHolder(
    innerTextField: @Composable (() -> Unit),
    text: String,
    placeholder: String,
    singleLine: Boolean,
    modifier: Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart
    ) {
        innerTextField()
        if (text.isEmpty()) {
            MenaText(
                text = placeholder,
                color = Theme.colorScheme.shadeTertiary,
                style = Theme.typography.label.medium
            )
        }
    }
}

@Composable
private fun VerticalDivider() {
    Box(
        Modifier
            .padding(horizontal = 8.dp)
            .size(1.dp, 21.dp)
            .background(Theme.colorScheme.stroke),
    )
}

@Preview
@Composable
private fun PreviewTextField() {
    MenaTheme {
        val (value, onValueChanged) = remember {
            mutableStateOf("")
        }
        BasicTextField(
            value = value,
            placeholder = "Placeholder",
            onValueChanged = onValueChanged,
            leadingIcon = painterResource(Res.drawable.ic_user),
            trailingIcon = painterResource(Res.drawable.silver_tc),
            isError = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}