package net.thechance.mena.faith.presentation.feature.quran.downloadedSur.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.remove
import net.thechance.mena.designsystem.presentation.component.dialog.Dialog
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource

@Composable
fun ScaffoldScope.DeleteConfirmationDialog(
    showDialog: Boolean,
    onDeleteClick: () -> Unit,
    onDismiss: () -> Unit,
    title: String ,
    message: String,
) {
    Dialog(
        isVisible = showDialog,
        onDismiss = onDismiss,
        title = title,
        message = message,
        dismissOnClickOutside = true,
        dismissOnBackPress = true,
        onCancelClick = onDismiss,
        actionButtons = {
            Text(
                text = stringResource(Res.string.remove),
                color = Theme.colorScheme.error,
                style = Theme.typography.label.medium,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 24.dp, bottom = 12.dp, end = 8.dp)
                    .clickable(
                        onClick = onDeleteClick,
                        role = Role.Button,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            )
        }
    )
}
