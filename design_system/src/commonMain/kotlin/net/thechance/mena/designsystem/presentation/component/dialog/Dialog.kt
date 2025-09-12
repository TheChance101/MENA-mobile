@file:OptIn(ExperimentalComposeUiApi::class)

package net.thechance.mena.designsystem.presentation.component.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import mena.design_system.generated.resources.Res
import mena.design_system.generated.resources.cancel_dialog_icon
import mena.design_system.generated.resources.ic_cancel
import net.thechance.mena.designsystem.presentation.component.appBar.HomeAppBar
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun Dialog(
    visible: Boolean,
    title: String,
    message: String,
    buttonText: String,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    onDismiss: () -> Unit,
    onActionClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    contentColor: Color = Theme.colorScheme.background.surfaceLow,
    dialogCornerShape: Shape = RoundedCornerShape(Theme.radius.xl),
    cancelBackgroundShape: Shape = RoundedCornerShape(Theme.radius.full),
    contentPadding: PaddingValues = PaddingValues(12.dp),
) {
    if (!visible) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                enabled = dismissOnClickOutside,
                onClick = onDismiss,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    )

    DialogContent(
        title = title,
        message = message,
        buttonText = buttonText,
        cancelBackgroundShape = cancelBackgroundShape,
        onActionClick = onActionClick,
        onCancelClick = onCancelClick,
        contentColor = contentColor,
        dialogCornerShape = dialogCornerShape,
        contentPadding = contentPadding,
        modifier = modifier
            .clickable(
                enabled = false,
                onClick = {},
            )
            .padding(horizontal = 16.dp)
    )

    if (dismissOnBackPress) {
        BackHandler(true) {
            onDismiss()
        }
    }
}

@Composable
private fun DialogContent(
    title: String,
    message: String,
    buttonText: String,
    contentColor: Color,
    dialogCornerShape: Shape,
    cancelBackgroundShape: Shape,
    contentPadding: PaddingValues,
    onActionClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(contentColor, dialogCornerShape)
            .padding(contentPadding)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_cancel),
                contentDescription = stringResource(Res.string.cancel_dialog_icon),
                modifier = Modifier
                    .clip(cancelBackgroundShape)
                    .clickable(
                        onClick = onCancelClick,
                        indication = ripple(),
                        interactionSource = remember { MutableInteractionSource() }
                    )
                    .background(
                        Theme.colorScheme.background.surface,
                        cancelBackgroundShape
                    )
                    .padding(PaddingValues(8.dp))
            )
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
    }
}


@Preview
@Composable
private fun DialoguePreview() {
    MenaTheme {
        var showDialog by remember { mutableStateOf(true) }

        DialogScaffold(
            parentContent = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = {
                            showDialog = true
                        })
                        .background(Theme.colorScheme.background.surface).fillMaxSize()
                ) {
                    HomeAppBar("202")
                    MenaText(
                        text = "HI",
                        style = Theme.typography.title.large,
                    )
                }
            },
            dialogContent = {
                Dialog(
                    visible = showDialog,
                    onDismiss = {
                        showDialog = false
                    },
                    title = "Preview Title",
                    message = "This is a preview message.",
                    buttonText = "Confirm",
                    dismissOnClickOutside = true,
                    dismissOnBackPress = true,
                    onActionClick = {
                        showDialog = false
                    },
                    onCancelClick = {
                        showDialog = false
                    },
                )
            }
        )
    }
}
