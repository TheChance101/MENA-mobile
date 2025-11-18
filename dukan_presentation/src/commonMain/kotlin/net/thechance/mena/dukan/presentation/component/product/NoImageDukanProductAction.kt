package net.thechance.mena.dukan.presentation.component.product

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_icon
import mena.dukan_presentation.generated.resources.add_product
import mena.dukan_presentation.generated.resources.ic_shopping_bag_add
import mena.dukan_presentation.generated.resources.remove_01
import mena.dukan_presentation.generated.resources.remove_product
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.animation.fadeTransitionSpec
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoImageDukanProductAction(
    showProductQuantity: Boolean,
    inCartQuantity: Int,
    onAddToCartClick: () -> Unit,
    onPlusClick: () -> Unit,
    onMinusClick: () -> Unit,
    dukanColor: Color
) {

    AnimatedContent(
        targetState = showProductQuantity,
        transitionSpec = { fadeTransitionSpec() },
        label = "CartToQuantity"
    ) {
        if (it) {
            ProductQuantityButton(
                inCartQuantity = inCartQuantity,
                onPlusClick = onPlusClick,
                onMinusClick = onMinusClick,
                dukanColor = dukanColor
            )
        } else {
            ProductCart(
                onClick = onAddToCartClick,
                dukanColor = dukanColor
            )
        }
    }
}

@Composable
private fun ProductCart(
    onClick: () -> Unit,
    dukanColor: Color,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(Res.drawable.ic_shopping_bag_add),
        tint = dukanColor,
        contentDescription = stringResource(Res.string.add_product),
        modifier = modifier.size(36.dp)
            .clip(RoundedCornerShape(Theme.radius.md))
            .border(
                1.dp,
                Theme.colorScheme.stroke,
                RoundedCornerShape(Theme.radius.md)
            )
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = null
            )
            .padding(Theme.spacing._8)
    )

}

@Composable
private fun ProductQuantityButton(
    onPlusClick: () -> Unit,
    onMinusClick: () -> Unit,
    inCartQuantity: Int,
    modifier: Modifier = Modifier,
    dukanColor: Color,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)) {
        Icon(
            painter = painterResource(Res.drawable.remove_01),
            tint = dukanColor,
            contentDescription = stringResource(Res.string.remove_product),
            modifier = modifier.size(24.dp)
                .clip(CircleShape)
                .background(Theme.colorScheme.background.surfaceLow)
                .clickable(
                    onClick = onMinusClick,
                    indication = null,
                    interactionSource = null
                )
                .padding(Theme.spacing._4 + Theme.spacing._2)
        )

        Box(
            modifier = modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(dukanColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (inCartQuantity < 10) "0$inCartQuantity" else "$inCartQuantity",
                style = Theme.typography.label.small,
                color = Theme.colorScheme.primary.onPrimary,
                textAlign = TextAlign.Center
            )
        }
        Icon(
            painter = painterResource(Res.drawable.add_icon),
            tint = dukanColor,
            contentDescription = stringResource(Res.string.add_product),
            modifier = modifier.size(24.dp)
                .clip(CircleShape)
                .background(Theme.colorScheme.background.surfaceLow)
                .clickable(
                    onClick = onPlusClick,
                    indication = null,
                    interactionSource = null
                )
                .padding(Theme.spacing._4 + Theme.spacing._2)
        )
    }
}


@Preview()
@Composable
private fun ProductActionNoImageDukanPreview() {
    MenaTheme {
        NoImageDukanProductAction(
            inCartQuantity = 0,
            showProductQuantity = false,
            dukanColor = Color(0xFFFB5B5D),
            onAddToCartClick = {},
            onPlusClick = {},
            onMinusClick = {}
        )
    }
}

@Preview()
@Composable
private fun ProductActionHasQuantityNoImageDukanPreview() {
    MenaTheme {
        NoImageDukanProductAction(
            inCartQuantity = 20,
            showProductQuantity = true,
            dukanColor = Color(0xFFFB5B5D),
            onAddToCartClick = {},
            onPlusClick = {},
            onMinusClick = {}
        )
    }
}