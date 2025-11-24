package net.thechance.mena.admin_panel.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.cancel
import net.thechance.mena.admin_panel.resources.ic_store_remove
import net.thechance.mena.designsystem.presentation.component.button.OutlinedButton
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.dialog.BasicDialog
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ScaffoldScope.DukanStatusChangeDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onConfirmed: () -> Unit,
    reason: String,
    onReasonChanged: (String) -> Unit,
    title: String,
    description: String,
    reasonLabel: String,
    confirmButtonText: String,
    isConfirmButtonEnabled: Boolean,
    isConfirmButtonLoading: Boolean,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        Popup(
            alignment = Alignment.Center,
            onDismissRequest = {
                onReasonChanged("")
                onDismiss()
            },
            properties = PopupProperties(focusable = true)
        ) {
            BasicDialog(
                onDismiss = {
                    onReasonChanged("")
                    onDismiss()
                },
                isVisible = isVisible,
                contentColor = Theme.colorScheme.background.surface,
                contentPadding = PaddingValues(24.dp),
                hasDismissButton = false,
                modifier = modifier
                    .background(
                        color = Theme.colorScheme.background.surface,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .width(400.dp)
            ) {
                DialogContent(
                    onDismiss = {
                        onReasonChanged("")
                        onDismiss()
                    },
                    onConfirmed = {
                        onReasonChanged("")
                        onConfirmed()
                    },
                    reason = reason,
                    onReasonChanged = onReasonChanged,
                    title = title,
                    description = description,
                    reasonLabel = reasonLabel,
                    confirmButtonText = confirmButtonText,
                    isConfirmButtonEnabled = isConfirmButtonEnabled,
                    isConfirmButtonLoading = isConfirmButtonLoading
                )
            }
        }
    }
}

@Composable
private fun DialogContent(
    onDismiss: () -> Unit,
    onConfirmed: () -> Unit,
    reason: String,
    onReasonChanged: (String) -> Unit,
    title: String,
    description: String,
    reasonLabel: String,
    confirmButtonText: String,
    isConfirmButtonEnabled: Boolean,
    isConfirmButtonLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Theme.colorScheme.background.surface),
        horizontalAlignment = Alignment.Start
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_store_remove),
            contentDescription = title,
            modifier = Modifier
                .size(88.dp)
                .background(
                    color = Theme.colorScheme.background.bgError,
                    shape = CircleShape
                )
                .padding(20.dp),
            tint = Theme.colorScheme.error
        )

        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = title,
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary
        )

        Text(
            text = description,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary
        )

        Text(
            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
            text = reasonLabel,
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary
        )

        BasicTextField(
            modifier = Modifier
                .padding(bottom = 24.dp)
                .heightIn(min = 96.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(Theme.radius.md))
                .background(color = Theme.colorScheme.background.surfaceLow)
                .padding(8.dp),
            value = reason,
            onValueChange = { onReasonChanged(it) },
            textStyle = Theme.typography.body.small.copy(
                color = Theme.colorScheme.shadePrimary
            ),
            maxLines = 6,
            cursorBrush = SolidColor(Theme.colorScheme.primary.primary)
        )

        DialogButtons(
            onDismiss = onDismiss,
            onConfirm = onConfirmed,
            confirmButtonText = confirmButtonText,
            isConfirmBtnEnabled = isConfirmButtonEnabled,
            isConfirmBtnLoading = isConfirmButtonLoading
        )
    }
}

@Composable
private fun DialogButtons(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmButtonText: String,
    isConfirmBtnEnabled: Boolean,
    isConfirmBtnLoading: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        OutlinedButton(
            modifier = Modifier
                .padding(end = 8.dp)
                .heightIn(min = 48.dp)
                .widthIn(min = 83.dp),
            text = stringResource(Res.string.cancel),
            onClick = onDismiss,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        )

        PrimaryButton(
            modifier = Modifier
                .heightIn(min = 48.dp)
                .then(
                    if (isConfirmBtnLoading) Modifier.widthIn(max = 109.dp)
                    else Modifier.widthIn(min = 76.dp, max = 109.dp)
                ),
            text = confirmButtonText,
            onClick = onConfirm,
            isLoading = isConfirmBtnLoading,
            isEnabled = isConfirmBtnEnabled,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}