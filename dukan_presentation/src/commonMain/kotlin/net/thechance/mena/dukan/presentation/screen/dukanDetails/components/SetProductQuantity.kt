package net.thechance.mena.dukan.presentation.screen.dukanDetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_icon
import mena.dukan_presentation.generated.resources.add_product
import mena.dukan_presentation.generated.resources.remove_01
import mena.dukan_presentation.generated.resources.remove_product
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SetProductQuantity(
    modifier: Modifier = Modifier,
    onAddProductClick: () -> Unit,
    onRemoveProductClick: () -> Unit
) {
    Row(
        modifier = modifier.background(
            color = Theme.colorScheme.background.surface,
            shape = RoundedCornerShape(size = Theme.radius.full)
        ).padding(vertical = Theme.spacing._2, horizontal = Theme.spacing._2),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(Res.drawable.remove_01),
            contentDescription = stringResource(Res.string.remove_product) ,
            tint = Theme.colorScheme.primary.primary,
            modifier = modifier
                .clip(RoundedCornerShape(size = Theme.radius.full))
                .background(
                    color = Theme.colorScheme.background.surfaceLow,
                )
                .clickable {onRemoveProductClick()}
                .padding(6.dp)
        )
        Text(
            text = "01",
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadePrimary
        )
        Icon(
            painter = painterResource(Res.drawable.add_icon),
            contentDescription = stringResource(Res.string.add_product),
            modifier = modifier
                .clip(RoundedCornerShape(size = Theme.radius.full))
                .background(
                    color = Theme.colorScheme.background.surfaceLow,
                )
                .clickable {onAddProductClick()}
                .padding(6.dp)
        )

    }
}