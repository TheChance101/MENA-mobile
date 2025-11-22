package net.thechance.mena.designsystem.presentation.component.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.appBar.HomeAppBar
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

@Deprecated("Use new Dialog instead")
@Composable
fun ScaffoldScope.Dialog(
    title: String,
    message: String,
    buttonText: String,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    hasDismissButton: Boolean = true,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    onDismiss: () -> Unit,
    onActionClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    contentColor: Color = Theme.colorScheme.background.surfaceLow,
    scrimColor: Color = Color.Black.copy(0.55f),
    dialogCornerShape: Shape = RoundedCornerShape(Theme.radius.xl),
    cancelBackgroundShape: Shape = RoundedCornerShape(Theme.radius.full),
    contentPadding: PaddingValues = PaddingValues(12.dp),
) {

    BasicDialog(
        isVisible = isVisible,
        onDismiss = onDismiss,
        hasDismissButton = hasDismissButton,
        dismissOnBackPress = dismissOnBackPress,
        dismissOnClickOutside = dismissOnClickOutside,
        contentColor = contentColor,
        scrimColor = scrimColor,
        dialogCornerShape = dialogCornerShape,
        cancelBackgroundShape = cancelBackgroundShape,
        contentPadding = contentPadding,
        onCancelClick = onCancelClick,
        actionButtons = {
            Text(
                text = buttonText,
                color = Theme.colorScheme.error,
                style = Theme.typography.label.medium,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 24.dp, bottom = 12.dp, end = 8.dp)
                    .clickable(
                        onClick = onActionClick,
                        role = Role.Button,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            )
        },
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 12.dp)
        ) {
            Text(
                text = title,
                style = Theme.typography.title.medium,
                color = Theme.colorScheme.primary.primary,
            )
            Text(
                text = message,
                style = Theme.typography.body.small,
                color = Theme.colorScheme.shadeSecondary,
            )
        }
    }
}

@Composable
fun ScaffoldScope.Dialog(
    title: String,
    message: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    actionButtons: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    hasDismissButton: Boolean = true,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    onCancelClick: () -> Unit = {},
    contentColor: Color = Theme.colorScheme.background.surfaceLow,
    scrimColor: Color = Color.Black.copy(0.55f),
    dialogCornerShape: Shape = SquircleShape(Theme.radius.xl),
    cancelBackgroundShape: Shape = RoundedCornerShape(Theme.radius.full),
    contentPadding: PaddingValues = PaddingValues(12.dp),
) {
    BasicDialog(
        onDismiss = onDismiss,
        isVisible = isVisible,
        hasDismissButton = hasDismissButton,
        dismissOnBackPress = dismissOnBackPress,
        dismissOnClickOutside = dismissOnClickOutside,
        contentColor = contentColor,
        scrimColor = scrimColor,
        dialogCornerShape = dialogCornerShape,
        cancelBackgroundShape = cancelBackgroundShape,
        contentPadding = contentPadding,
        onCancelClick = onCancelClick,
        actionButtons = actionButtons,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 12.dp)
        ) {
            Text(
                text = title,
                style = Theme.typography.title.medium,
                color = Theme.colorScheme.primary.primary,
            )
            Text(
                text = message,
                textAlign = TextAlign.Center,
                style = Theme.typography.body.small,
                color = Theme.colorScheme.shadeSecondary,
            )
        }
    }
}

@Preview(showBackground = true,backgroundColor = 0xFFF2F4F7)
@Composable
private fun DialoguePreview() {
    MenaTheme {
        var showDialog by remember { mutableStateOf(true) }

        Scaffold(
            topBar = { HomeAppBar("202") },
            overlays = {
                dialog(
                    isVisible = showDialog
                ) {
                    Dialog(
                        isVisible = showDialog,
                        onDismiss = { showDialog = false },
                        title = "Preview Title",
                        message = "This is a preview message.",
                        dismissOnClickOutside = true,
                        dismissOnBackPress = true,
                        onCancelClick = { showDialog = false },
                        actionButtons = {
                            Text(
                                text = "Confirm",
                                color = Theme.colorScheme.error,
                                style = Theme.typography.label.medium,
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(top = 24.dp, bottom = 12.dp, end = 8.dp)
                                    .clickable(
                                        onClick = { showDialog = false },
                                        role = Role.Button,
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    )
                            )
                        }
                    )
                }
            }) {
            Text(
                text = "HI",
                style = Theme.typography.title.large,
                modifier = Modifier.clickable { showDialog = true }
            )
        }
    }
}