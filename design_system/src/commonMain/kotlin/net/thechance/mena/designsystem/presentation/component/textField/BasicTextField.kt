package net.thechance.mena.designsystem.presentation.component.textField

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
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
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BasicTextField(
    value: String,
    hint: String,
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
    Column(modifier) {
        title?.let {
            Text(
                text = title,
                style = Theme.typography.title.small,
                modifier = Modifier.padding(bottom = 4.dp),
                color = Theme.colorScheme.shadePrimary,
                fontSize = 16.sp,
                letterSpacing = 0.sp,
                lineHeight = 22.sp
            )
        }

        Row(Modifier.fillMaxWidth()) {
            leadingContent?.let {
                leadingContent()
                Spacer(Modifier.width(Theme.spacing._4))
            }

            BasicTextField(
                value = value,
                onValueChange = {
                    if (it.length <= maxCharacters)
                        onValueChanged(it)
                },
                enabled = enabled,
                readOnly = readOnly,
                minLines = minLines,
                maxLines = if (singleLine) 1 else maxLines,
                textStyle = Theme.typography.body.small.copy(
                    color = Theme.colorScheme.shadePrimary
                ),
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
                        hint = hint,
                        leadingIcon = leadingIcon,
                        trailingIcon = trailingIcon,
                        onTrailingIconClick = onTrailingIconClick,
                        showTrailingDivider = showTrailingDivider,
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
            Text(
                text = errorMessage,
                style = Theme.typography.label.small,
                modifier = Modifier.padding(
                    start = 12.dp,
                    top = 2.dp
                ),
                color = Theme.colorScheme.error
            )
        }
    }
}

@Composable
private fun TextFieldContent(
    innerTextField: @Composable () -> Unit,
    text: String,
    hint: String,
    leadingIcon: Painter?,
    trailingIcon: Painter?,
    leadingIconTint: Color,
    isError: Boolean,
    singleLine: Boolean,
    showTrailingDivider: Boolean = true,
    onTrailingIconClick: (() -> Unit)? = null,
) {

    val animatedIconErrorColor by animateColorAsState(
        targetValue = if (isError) Theme.colorScheme.error else leadingIconTint

    )
    Row(
        modifier = Modifier.padding(12.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon?.let {

            Icon(
                painter = leadingIcon,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = Theme.spacing._8)
                    .size(24.dp),
                tint = animatedIconErrorColor
            )
        }

        InnerTextFieldWithHint(
            innerTextField = innerTextField,
            text = text,
            hint = hint,
            singleLine = singleLine,
            modifier = Modifier.weight(1f)
        )

        trailingIcon?.let {
            if (showTrailingDivider)
                VerticalDivider()
            Image(
                painter = trailingIcon,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(
                        enabled = onTrailingIconClick != null,
                        indication = null,
                        interactionSource = remember {
                            MutableInteractionSource()
                        }
                    ) {
                        onTrailingIconClick?.invoke()
                    }
            )
        }
    }

}

@Composable
private fun InnerTextFieldWithHint(
    innerTextField: @Composable (() -> Unit),
    text: String,
    hint: String,
    singleLine: Boolean,
    modifier: Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart
    ) {
        innerTextField()
        if (text.isEmpty()) {
            Text(
                text = hint,
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.shadeTertiary
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
            hint = "hint",
            onValueChanged = onValueChanged,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = painterResource(Res.drawable.ic_user),
            trailingIcon = painterResource(Res.drawable.silver_tc),
            isError = true,
            onTrailingIconClick = {},
        )
    }
}