package net.thechance.mena.dukan.presentation.component.product

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_shopping_basket
import mena.dukan_presentation.generated.resources.ic_add_shopping_basket
import mena.dukan_presentation.generated.resources.wide_image_shoppingcart
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.animation.fadeTransitionSpec
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SmallAndWideImageDukanProductAction(
    showProductQuantity: Boolean,
    inCartQuantity: Int,
    onAddToCartClick: () -> Unit,
    onPlusClick: () -> Unit,
    onMinusClick: () -> Unit,
    cartIcon: Painter,
    dukanColor: Color,
    modifier: Modifier = Modifier
) {

    AnimatedContent(
        modifier = modifier,
        targetState = showProductQuantity,
        transitionSpec = { fadeTransitionSpec() },
        label = "CartToQuantity"
    ) {
        if (it) {
            ProductQuantityButton(
                inCartQuantity = inCartQuantity,
                onPlusClick = onPlusClick,
                onMinusClick = onMinusClick,
            )
        } else {
            ProductCart(
                dukanColor = dukanColor,
                cartIcon = cartIcon,
                onClick = onAddToCartClick
            )
        }
    }
}

@Composable
private fun ProductCart(
    onClick: () -> Unit,
    cartIcon: Painter,
    dukanColor: Color
) {
    Icon(
        painter = cartIcon,
        contentDescription = stringResource(Res.string.add_shopping_basket),
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(dukanColor)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = MutableInteractionSource()
            )
            .padding(Theme.spacing._8)
    )

}

@Preview
@Composable
private fun ProductActionIconSmallImageDukanPreview() {
    MenaTheme {
        SmallAndWideImageDukanProductAction(
            showProductQuantity = false,
            inCartQuantity = 0,
            dukanColor = Color(0xFFFB5B5D),
            cartIcon = painterResource(Res.drawable.ic_add_shopping_basket),
            onAddToCartClick = {},
            onPlusClick = {},
            onMinusClick = {}
        )
    }
}

@Preview
@Composable
private fun ProductActionIconWideImageDukanPreview() {
    MenaTheme {
        SmallAndWideImageDukanProductAction(
            showProductQuantity = false,
            inCartQuantity = 0,
            dukanColor = Color(0xFFFB5B5D),
            cartIcon = painterResource(Res.drawable.wide_image_shoppingcart),
            onAddToCartClick = {},
            onPlusClick = {},
            onMinusClick = {}
        )
    }
}

