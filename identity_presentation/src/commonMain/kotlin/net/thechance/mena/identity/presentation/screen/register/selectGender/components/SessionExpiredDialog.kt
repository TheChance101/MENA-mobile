package net.thechance.mena.identity.presentation.screen.register.selectGender.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error
import mena.identity_presentation.generated.resources.ok
import mena.identity_presentation.generated.resources.session_expired
import net.thechance.mena.designsystem.presentation.component.button.TextButton
import net.thechance.mena.designsystem.presentation.component.dialog.Dialog
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import org.jetbrains.compose.resources.stringResource

@Composable
fun ScaffoldScope.SessionExpiredDialog(
    isVisible: Boolean,
    onClick: () -> Unit,
) {
    Dialog(
        title = stringResource(Res.string.error),
        message = stringResource(Res.string.session_expired),
        hasDismissButton = true,
        dismissOnClickOutside = false,
        isVisible = isVisible,
        onDismiss = {},
        onCancelClick = onClick::invoke,
        actionButtons = {
            TextButton(
                text = stringResource(Res.string.ok),
                onClick = onClick::invoke,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 24.dp, end = 12.dp, bottom = 12.dp)
            )
        }
    )
}