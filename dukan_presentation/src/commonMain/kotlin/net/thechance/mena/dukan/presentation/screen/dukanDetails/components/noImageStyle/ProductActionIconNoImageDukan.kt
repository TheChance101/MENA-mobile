package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.noImageStyle

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_product
import mena.dukan_presentation.generated.resources.ic_shopping_bag_add
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProductActionIconNoImageDukan(
    dukanColor: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(Res.drawable.ic_shopping_bag_add),
        tint = Color(dukanColor),
        contentDescription = stringResource(Res.string.add_product),
        modifier = modifier.size(36.dp)
            .clip(RoundedCornerShape(Theme.radius.md))
            .clickable(onClick = onClick, indication = null, interactionSource = null)
            .border(
                1.dp,
                Theme.colorScheme.stroke,
                RoundedCornerShape(Theme.radius.md)
            )

            .padding(Theme.spacing._8)
    )
}