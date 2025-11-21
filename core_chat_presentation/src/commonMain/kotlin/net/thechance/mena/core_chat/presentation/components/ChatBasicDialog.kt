package net.thechance.mena.core_chat.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_cancel
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatBasicDialog(
    onDismiss: () -> Unit,
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    hasDismissButton: Boolean = true,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    contentColor: Color = Theme.colorScheme.background.surfaceLow,
    scrimColor: Color = Theme.colorScheme.primary.primary.copy(0.55f),
    dialogCornerShape: Shape = RoundedCornerShape(Theme.radius.xl),
    cancelBackgroundShape: Shape = RoundedCornerShape(Theme.radius.full),
    contentPadding: PaddingValues = PaddingValues(12.dp),
    onCancelClick: () -> Unit = {},
    actionButtons: @Composable ColumnScope.() -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween()),
        exit = fadeOut(tween())
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .clickable(
                        enabled = dismissOnClickOutside,
                        onClick = onDismiss,
                    )
                    .fillMaxSize()
                    .background(scrimColor)
                    .animateEnterExit(
                        enter = fadeIn(),
                        exit = fadeOut()
                    )
            )

            DialogContent(
                hasDismissButton = hasDismissButton,
                cancelBackgroundShape = cancelBackgroundShape,
                contentColor = contentColor,
                dialogCornerShape = dialogCornerShape,
                contentPadding = contentPadding,
                content = content,
                onCancelClick = onCancelClick,
                actionButtons = actionButtons,
                modifier = modifier
                    .animateEnterExit(
                        enter = scaleIn(),
                        exit = scaleOut()
                    )
                    .padding(horizontal = 16.dp)
                    .clickable(enabled = false) { }
            )

            if (dismissOnBackPress) {
                BackHandler(
                    enabled = true,
                    onBack = onDismiss
                )
            }
        }
    }
}

@Composable
private fun DialogContent(
    hasDismissButton: Boolean,
    contentColor: Color,
    dialogCornerShape: Shape,
    cancelBackgroundShape: Shape,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    onCancelClick: () -> Unit,
    actionButtons: @Composable ColumnScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Column(
        modifier = modifier
            .background(contentColor, dialogCornerShape)
            .padding(contentPadding)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (hasDismissButton) {
                Icon(
                    painter = painterResource(Res.drawable.ic_cancel),
                    tint = Theme.colorScheme.primary.primary,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(cancelBackgroundShape)
                        .clickable(onClick = onCancelClick)
                        .background(
                            Theme.colorScheme.background.surface,
                            cancelBackgroundShape
                        )
                        .padding(PaddingValues(8.dp))
                )
            }
            content()
        }
        actionButtons()
    }
}