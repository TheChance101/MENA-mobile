package net.thechance.mena.designsystem.presentation.component.snackbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import mena.design_system.generated.resources.Res
import mena.design_system.generated.resources.ic_check_circle
import mena.design_system.generated.resources.ic_close_circle
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SnackBar(
    isVisible: Boolean = false,
    title: String,
    message: String,
    leadingIcon: Painter,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
    contentDescription: String? = null,
    displayDurationMs: Long = 3000L,
    hideAnimationDelayMs: Long = 200L,  
    onDismiss: () -> Unit,
    onClick: () -> Unit = {},
) {
    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(displayDurationMs)
            delay(hideAnimationDelayMs)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Theme.radius.md))
                .background(Theme.colorScheme.background.surfaceLow)
                .clickable {
                    onClick()
                    onDismiss()
                }
                .padding(
                    top = Theme.spacing._8,
                    bottom = Theme.spacing._8,
                    start = Theme.spacing._12,
                    end = Theme.spacing._24
                )
        ) {
            Icon(
                painter = leadingIcon,
                contentDescription = contentDescription,
                modifier = Modifier.size(28.dp),
                tint = tint
            )

            Column {
                Text(
                    text = title,
                    style = Theme.typography.label.large,
                    color = Theme.colorScheme.shadePrimary,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )

                Text(
                    text = message,
                    style = Theme.typography.body.small,
                    color = Theme.colorScheme.shadeSecondary
                )
            }
        }
    }
}

@Deprecated(
    message = "Use the new SnackBar with animation and auto-dismiss functionality. " +
            "This static version lacks visibility control and animations.",
    replaceWith = ReplaceWith(
        expression = "SnackBar(isVisible = true, title = title, message = message, leadingIcon = leadingIcon, modifier = modifier, tint = tint, contentDescription = contentDescription)",
    ),
    level = DeprecationLevel.WARNING
)
@Composable
fun SnackBar(
    title: String,
    message: String,
    leadingIcon: Painter,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
    contentDescription: String? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surfaceLow)
            .padding(
                top = Theme.spacing._8,
                bottom = Theme.spacing._8,
                start = Theme.spacing._12,
                end = Theme.spacing._24
            )
    ) {
        Icon(
            painter = leadingIcon,
            contentDescription = contentDescription,
            modifier = Modifier.size(28.dp),
            tint = tint
        )

        Column {
            Text(
                text = title,
                style = Theme.typography.label.large,
                color = Theme.colorScheme.shadePrimary,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )

            Text(
                text = message,
                style = Theme.typography.body.small,
                color = Theme.colorScheme.shadeSecondary
            )
        }
    }
}

@Preview
@Composable
private fun SnackBarPreview() {
    MenaTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SnackBar(
                    title = "Success",
                    message = "message description",
                    leadingIcon = painterResource(Res.drawable.ic_check_circle),
                    modifier = Modifier.fillMaxWidth()
                )

                SnackBar(
                    title = "Error",
                    message = "message description",
                    leadingIcon = painterResource(Res.drawable.ic_close_circle),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}