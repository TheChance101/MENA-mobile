package net.thechance.mena.admin_panel.presentation.screen.dukan_details.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.thechance.mena.admin_panel.presentation.component.StatePlaceholder
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.empty_shelf_img
import net.thechance.mena.admin_panel.resources.empty_shelf_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EmptyShelfScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        StatePlaceholder(
            image = painterResource(Res.drawable.empty_shelf_img),
            title = stringResource(Res.string.empty_shelf_title),
            description = "",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}