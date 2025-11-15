package net.thechance.mena.designsystem.presentation.component.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mena.design_system.generated.resources.Res
import mena.design_system.generated.resources.ic_cheese_cake
import net.thechance.mena.designsystem.presentation.component.button.content.BaseButtonContent
import net.thechance.mena.designsystem.presentation.component.preview.PreviewComponent
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: Painter? = null,
    iconSize: Dp = 20.dp,
    iconStartPadding: Dp = Theme.spacing._8,
    isEnabled: Boolean = true,
    isLoading: Boolean = false,
    containerColor: Color = Theme.colorScheme.primary.primary,
    disabledContainerColor: Color = Theme.colorScheme.disabled,
    contentColor: Color = Theme.colorScheme.primary.onPrimary,
    disabledContentColor: Color = Theme.colorScheme.textDisabled,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = Theme.spacing._16,
        vertical = 13.dp
    ),
    shape: Shape = SquircleShape(Theme.radius.md)
) {
    Button(
        isEnabled = isEnabled,
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContentColor = disabledContentColor,
        disabledContainerColor = disabledContainerColor,
        contentPadding = contentPadding,
        shape = shape,
        isLoading = isLoading,
        loadingColors = listOf(
            Theme.colorScheme.primary.onPrimaryHint,
            Theme.colorScheme.primary.onPrimaryBody,
            Theme.colorScheme.primary.onPrimary
        ),
        onClick = onClick,
        modifier = modifier
    ) {
        BaseButtonContent(
            text = text,
            contentColor = it,
            trailingIcon = trailingIcon,
            iconSize = iconSize,
            iconStartPadding = iconStartPadding
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimaryButtonPreview() {
    MenaTheme {
        var primaryButtonLoading by remember { mutableStateOf(false) }

        LaunchedEffect(primaryButtonLoading) {
            if (primaryButtonLoading) {
                launch {
                    delay(1000)
                    primaryButtonLoading = false
                }
            }
        }

        PreviewComponent(
            isScrollable = true,
            title = "Primary button"
        ) {
            PrimaryButton(
                text = "Button",
                trailingIcon = painterResource(resource = Res.drawable.ic_cheese_cake),
                onClick = {},
                modifier = Modifier
            )
            PrimaryButton(
                text = "Click me to test loading",
                isLoading = primaryButtonLoading,
                trailingIcon = painterResource(resource = Res.drawable.ic_cheese_cake),
                onClick = { primaryButtonLoading = !primaryButtonLoading },
            )
            PrimaryButton(
                text = "Button",
                trailingIcon = painterResource(resource = Res.drawable.ic_cheese_cake),
                onClick = {},
                isEnabled = false,
                modifier = Modifier
            )
        }
    }
}
