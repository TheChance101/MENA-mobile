package net.thechance.mena.admin_panel.presentation.screen.users_management.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.activate
import net.thechance.mena.admin_panel.resources.block
import net.thechance.mena.admin_panel.resources.ic_activate
import net.thechance.mena.admin_panel.resources.ic_block
import net.thechance.mena.designsystem.presentation.component.button.OutlinedButton
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserStatusToggleButton(
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val buttonText = if (isActive) {
        stringResource(Res.string.block)
    } else {
        stringResource(Res.string.activate)
    }
    val buttonTrailingIcon = if (isActive) {
        painterResource(Res.drawable.ic_block)
    } else {
        painterResource(Res.drawable.ic_activate)
    }

    OutlinedButton(
        text = buttonText,
        trailingIcon = buttonTrailingIcon,
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 13.dp),
        modifier = modifier.wrapContentWidth(),
    )
}