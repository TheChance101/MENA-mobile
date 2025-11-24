package net.thechance.mena.designsystem.presentation.component.textField

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import mena.design_system.generated.resources.Res
import mena.design_system.generated.resources.ic_arrow_down
import mena.design_system.generated.resources.ic_iraq
import mena.design_system.generated.resources.ic_profile
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MobileNumberTextField(
    value: String,
    hint: String,
    leadingIcon: Painter,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    leadingContent: (@Composable () -> Unit)? = null,
    maxCharacters: Int = 16,
    title: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    leadingIconTint: Color = Theme.colorScheme.shadePrimary,
    shape: Shape = RoundedCornerShape(Theme.radius.md),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester = FocusRequester(),
    onFocusChanged: (Boolean) -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    BasicTextField(
        value = value,
        onValueChanged = onValueChanged,
        hint = hint,
        leadingIcon = leadingIcon,
        trailingIcon = null,
        title = title,
        leadingContent = leadingContent,
        leadingIconTint = leadingIconTint,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        errorMessage = errorMessage,
        shape = shape,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        focusRequester = focusRequester,
        onFocusChanged = onFocusChanged,
        visualTransformation = visualTransformation,
        modifier = modifier,
        maxCharacters = maxCharacters,
    )
}

@Composable
fun MobileNumberLeadingContent(
    countryCode: String,
    countryPainter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(Theme.radius.md))
            .clickable(onClick = onClick)
            .background(Theme.colorScheme.background.surfaceLow)
            .padding(
                vertical = 13.dp,
                horizontal = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = countryPainter,
            contentDescription = "country image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .clip(RoundedCornerShape(Theme.radius.full))
                .size(20.dp)
        )

        Text(
            text = countryCode,
            style = Theme.typography.label.medium.copy(
                textDirection = TextDirection.Ltr
            ),
            modifier = Modifier.padding(start = 4.dp, end = 2.dp),
            color = Theme.colorScheme.shadePrimary,
        )

        Icon(
            painter = painterResource(Res.drawable.ic_arrow_down),
            contentDescription = "arrow down",
            tint = Theme.colorScheme.shadePrimary,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Preview(showBackground = true,backgroundColor = 0xFFF2F4F7)
@Composable
fun PreviewMobileNumberTextField() {
    MenaTheme {
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