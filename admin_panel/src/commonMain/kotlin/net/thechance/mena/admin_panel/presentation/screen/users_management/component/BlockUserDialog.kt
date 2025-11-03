package net.thechance.mena.admin_panel.presentation.screen.users_management.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.block
import net.thechance.mena.admin_panel.resources.block_user
import net.thechance.mena.admin_panel.resources.block_user_confirmation_message
import net.thechance.mena.admin_panel.resources.cancel
import net.thechance.mena.admin_panel.resources.ic_block
import net.thechance.mena.admin_panel.resources.ic_user_block
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
fun ScaffoldScope.BlockUserDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onConfirmBlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    BasicDialog(
        onDismiss = onDismiss,
        isVisible = isVisible,
        modifier = modifier.fillMaxWidth(0.35f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Theme.colorScheme.background.surfaceLow,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            DialogIcon()
            DialogContent()
            DialogActions(onCancel = onDismiss, onConfirmBlock = onConfirmBlock)
        }
    }
}

@Composable
private fun DialogIcon() {
    Icon(
        painter = painterResource(Res.drawable.ic_user_block),
        contentDescription = null,
        modifier = Modifier
            .size(88.dp)
            .background(
                color = Theme.colorScheme.background.bgError,
                shape = CircleShape
            )
            .padding(20.dp),
        tint = Theme.colorScheme.error
    )
}

@Composable
private fun DialogContent() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 24.dp)
    ) {
        Text(
            text = stringResource(Res.string.block_user),
            style = Theme.typography.title.medium,
        )

        Text(
            text = stringResource(Res.string.block_user_confirmation_message),
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary
        )
    }
}

@Composable
private fun DialogActions(
    onCancel: () -> Unit,
    onConfirmBlock: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PrimaryButton(
            text = stringResource(Res.string.cancel),
            onClick = onCancel,
            modifier = Modifier.height(48.dp)
        )

        OutlinedButton(
            text = stringResource(Res.string.block),
            onClick = onConfirmBlock,
            trailingIcon = painterResource(Res.drawable.ic_block),
            modifier = Modifier.height(48.dp)
        )
    }
}