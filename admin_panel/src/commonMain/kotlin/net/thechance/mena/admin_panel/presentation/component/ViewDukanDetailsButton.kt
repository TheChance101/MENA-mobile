package net.thechance.mena.admin_panel.presentation.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.ic_view_details
import net.thechance.mena.admin_panel.resources.view_details
import net.thechance.mena.designsystem.presentation.component.button.OutlinedButton
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ViewDukanDetailsButton(
    onClick: () -> Unit, modifier: Modifier = Modifier
) {
    OutlinedButton(
        text = stringResource(Res.string.view_details),
        trailingIcon = painterResource(Res.drawable.ic_view_details),
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 13.dp),
        modifier = modifier.wrapContentWidth(),
    )
}