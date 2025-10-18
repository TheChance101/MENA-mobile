package net.thechance.mena.dukan.presentation.screen.dukanDetails.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_shopping_basket
import mena.dukan_presentation.generated.resources.ic_add_shopping_basket
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.animation.fadeTransitionSpec
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProductActionIconSmallImageDukan(
    inCartQuantity: Int,
    onAddClick: () -> Unit,
    onPlusClick: () -> Unit,
    onMinusClick: () -> Unit,
    cartColor: Color? = null
) {

    AnimatedContent(
        targetState = inCartQuantity > 0,
        transitionSpec = { fadeTransitionSpec() },
        label = "CartToQuantity"
    ) {
        if (it) {
            SetProductQuantity(
                onAddProductClick = onPlusClick,
                onRemoveProductClick = onMinusClick,
                cartColor = cartColor
            )
        } else {
            ProductCart(
                cartColor = cartColor,
                onClick = onAddClick
            )
        }
    }
}

@Composable
private fun ProductCart(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cartColor: Color? = null
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(RoundedCornerShape(size = Theme.radius.full))
            .background(
                color = cartColor ?: Theme.colorScheme.primary.primary,
            )
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_add_shopping_basket),
            contentDescription = stringResource(Res.string.add_shopping_basket),
            modifier = modifier
                .size(16.dp)
        )
    }
}

@Preview()
@Composable
private fun ProductActionIconSmallImageDukanPreview() {
    MenaTheme {
        ProductActionIconSmallImageDukan(
            inCartQuantity = 0,
            cartColor = Color(0xFFFB5B5D),
            onAddClick = {},
            onPlusClick = {},
            onMinusClick = {}
        )
    }
}

@Preview()
@Composable
private fun ProductActionHasQuantitySmallImageDukanPreview() {
    MenaTheme {
        ProductActionIconSmallImageDukan(
            inCartQuantity = 1,
            cartColor = Color(0xFFFB5B5D),
            onAddClick = {},
            onPlusClick = {},
            onMinusClick = {}
        )
    }
}