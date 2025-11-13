package net.thechance.mena.dukan.presentation.screen.dukanCart.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.delete_icon
import mena.dukan_presentation.generated.resources.ic_delete
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sv.lib.squircleshape.SquircleShape

@Composable
fun DukanCartDeleteActionButton(
    onRemoveItemClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(Res.drawable.ic_delete),
        contentDescription = stringResource(Res.string.delete_icon),
        tint = Theme.colorScheme.error,
        modifier = modifier
            .padding(vertical = Theme.spacing._8)
            .width(48.dp)
            .clip(
                SquircleShape(
                    topEnd = Theme.radius.md,
                    bottomEnd = Theme.radius.md
                )
            )
            .background(Theme.colorScheme.background.bgError)
            .clickable(onClick = { onRemoveItemClicked() })
            .padding(
                vertical = Theme.spacing._32,
                horizontal = Theme.spacing._12
            )
    )
}