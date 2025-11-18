package net.thechance.mena.dukan.presentation.component.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_icon
import mena.dukan_presentation.generated.resources.add_product
import mena.dukan_presentation.generated.resources.ic_add_shopping_basket
import mena.dukan_presentation.generated.resources.remove_01
import mena.dukan_presentation.generated.resources.remove_product
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProductQuantityButton(
    onPlusClick: () -> Unit,
    onMinusClick: () -> Unit,
    inCartQuantity: Int,
    dukanColor: Color = Theme.colorScheme.primary.primary,
    backgroundColor: Color = Theme.colorScheme.background.surface,
    iconPadding: PaddingValues = PaddingValues(Theme.spacing._4 + Theme.spacing._2)
) {
    Row(
        modifier = Modifier.background(
            color = backgroundColor,
            shape = CircleShape
        ).padding(vertical = Theme.spacing._2, horizontal = Theme.spacing._2),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(Res.drawable.remove_01),
            contentDescription = stringResource(Res.string.remove_product),
            tint = dukanColor,
            modifier = Modifier
                .clip(CircleShape)
                .background(color = Theme.colorScheme.background.surfaceLow)
                .clickable(
                    onClick = onMinusClick,
                    indication = null,
                    interactionSource = MutableInteractionSource()
                )
                .padding(iconPadding)
        )
        Text(
            text = if (inCartQuantity < 10 && inCartQuantity != 0) "0$inCartQuantity" else "$inCartQuantity",
            style = Theme.typography.label.small,
            color = Theme.colorScheme.primary.primary,
            modifier = Modifier.padding(horizontal = Theme.spacing._4)
        )
        Icon(
            painter = painterResource(Res.drawable.add_icon),
            contentDescription = stringResource(Res.string.add_product),
            tint = dukanColor,
            modifier = Modifier
                .clip(CircleShape)
                .background(color = Theme.colorScheme.background.surfaceLow)
                .clickable(
                    onClick = onPlusClick,
                    indication = null,
                    interactionSource = MutableInteractionSource()
                )
                .padding(iconPadding)
        )

    }
}

@Preview
@Composable
private fun ProductActionHasQuantitySmallImageDukanPreview() {
    MenaTheme {
        SmallAndWideImageDukanProductAction(
            showProductQuantity = true,
            inCartQuantity = 1,
            dukanColor = Color(0xFFFB5B5D),
            cartIcon = painterResource(Res.drawable.ic_add_shopping_basket),
            onAddToCartClick = {},
            onPlusClick = {},
            onMinusClick = {}
        )
    }
}