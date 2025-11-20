package net.thechance.mena.designsystem.presentation.component.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mena.design_system.generated.resources.Res
import mena.design_system.generated.resources.ic_cheese_cake
import net.thechance.mena.designsystem.presentation.component.button.content.BaseButtonContent
import net.thechance.mena.designsystem.presentation.component.preview.PreviewComponent
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TextButton(
    text: String,
    onClick: () -> Unit,
    trailingIcon: Painter? = null,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isLoading: Boolean = false,
    contentColor: Color = Theme.colorScheme.primary.primary,
    disabledContentColor: Color = Theme.colorScheme.disabled,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    iconSize: Dp = 16.dp,
    iconStartPadding: Dp = Theme.spacing._4,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    Button(
        onClick = onClick,
        isEnabled = isEnabled,
        contentPadding = contentPadding,
        contentColor = contentColor,
        disabledContentColor = disabledContentColor,
        shape = RoundedCornerShape(Theme.radius.xxs),
        isLoading = isLoading,
        loadingColors = listOf(
            Theme.colorScheme.stroke,
            Theme.colorScheme.shadeTertiary,
            Theme.colorScheme.primary.primary
        ),
        modifier = modifier
    ){
        BaseButtonContent(
            text = text,
            trailingIcon = trailingIcon,
            iconSize = iconSize,
            iconStartPadding = iconStartPadding,
            overflow = overflow,
            contentColor = it,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TextButtonPreview() {
    MenaTheme {
        PreviewComponent(
            isScrollable = true,
            title = "Text button"
        ) {
            TextButton(
                text = "Button",
                trailingIcon = painterResource(resource = Res.drawable.ic_cheese_cake),
                onClick = {},
                modifier = Modifier
            )
            TextButton(
                text = "Button",
                isLoading = true,
                trailingIcon = painterResource(resource = Res.drawable.ic_cheese_cake),
                onClick = {},
                modifier = Modifier
            )
            TextButton(
                text = "Button",
                trailingIcon = painterResource(resource = Res.drawable.ic_cheese_cake),
                onClick = {},
                isEnabled = false,
                modifier = Modifier
            )
        }
    }
}
