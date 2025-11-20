package net.thechance.mena.admin_panel.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.cancel
import net.thechance.mena.designsystem.presentation.component.button.OutlinedButton
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.dialog.BasicDialog
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource

@Composable
fun ScaffoldScope.AdminConfirmationDialog(
    dialogIcon: Painter,
    confirmationIcon: Painter,
    title: String,
    description: String,
    confirmationButtonText: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    scrimColor: Color = Theme.colorScheme.primary.primary.copy(.66f),
    modifier: Modifier = Modifier
) {
    Popup(
        alignment = Alignment.Center,
        onDismissRequest = onDismiss
    ){
        BasicDialog(
            onDismiss = onDismiss,
            isVisible = isVisible,
            dialogCornerShape = RoundedCornerShape(24.dp),
            contentPadding = PaddingValues(24.dp),
            hasDismissButton = false,
            scrimColor = scrimColor,
            contentColor = Theme.colorScheme.background.surfaceLow,
            actionButtons = {
                AdminConfirmationDialogActionButtons(
                    onDismiss = onDismiss,
                    onConfirm = onConfirm,
                    confirmationIcon = confirmationIcon,
                    confirmationButtonText = confirmationButtonText
                )
            },
            modifier = modifier
        ) {
            AdminConfirmationDialogContent(
                dialogIcon = dialogIcon,
                title = title,
                description = description
            )
        }
    }
}

@Composable
private fun AdminConfirmationDialogActionButtons(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmationIcon: Painter,
    confirmationButtonText: String
) {
    Row(
        modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PrimaryButton(
            text = stringResource(Res.string.cancel),
            onClick = onDismiss,
            contentPadding = PaddingValues(vertical = 13.dp, horizontal = 16.dp)
        )
        OutlinedButton(
            text = confirmationButtonText,
            onClick = onConfirm,
            trailingIcon = confirmationIcon,
            contentPadding = PaddingValues(vertical = 13.dp, horizontal = 16.dp)
        )
    }
}

@Composable
private fun AdminConfirmationDialogContent(
    dialogIcon: Painter,
    title: String,
    description: String,
) {
    Column(
        modifier = Modifier
            .padding(bottom = 12.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = Theme.colorScheme.background.bgError,
                    shape = CircleShape
                ).padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = dialogIcon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = Theme.colorScheme.error
            )
        }
        Column {
            Text(
                text = title,
                style = Theme.typography.title.medium,
                color = Theme.colorScheme.shadePrimary
            )
            Text(
                text = description,
                style = Theme.typography.body.small,
                color = Theme.colorScheme.shadeSecondary
            )
        }
    }
}