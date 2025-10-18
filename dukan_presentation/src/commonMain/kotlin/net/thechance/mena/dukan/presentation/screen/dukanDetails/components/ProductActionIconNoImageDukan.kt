package net.thechance.mena.dukan.presentation.screen.dukanDetails.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
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
fun ProductActionNoImageDukan(
    inCartQuantity: Int,
    dukanColor: Long,
    onAddClick: () -> Unit,
    onPlusClick: () -> Unit,
    onMinusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = inCartQuantity > 0,
        transitionSpec = { fadeTransitionSpec() },
        label = "CartToQuantity"
    ) {
        if (it) {
            Row(horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)) {
                Icon(
                    painter = painterResource(Res.drawable.remove_01),
                    tint = Color(dukanColor),
                    contentDescription = stringResource(Res.string.remove_product),
                    modifier = modifier.size(24.dp)
                        .clip(RoundedCornerShape(Theme.radius.full))
                        .background(Theme.colorScheme.background.surfaceLow)
                        .clickable(onClick = onMinusClick)
                        .padding(Theme.spacing._4 + Theme.spacing._2)
                )

                Text(
                    text = inCartQuantity.toString(),
                    style = Theme.typography.label.small,
                    color = Theme.colorScheme.primary.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = modifier.size(24.dp)
                        .clip(RoundedCornerShape(Theme.radius.full))
                        .background(Color(dukanColor))
                        .padding(vertical = Theme.spacing._4, horizontal = Theme.spacing._2)
                )

                Icon(
                    painter = painterResource(Res.drawable.add_icon),
                    tint = Color(dukanColor),
                    contentDescription = stringResource(Res.string.add_product),
                    modifier = modifier.size(24.dp)
                        .clip(RoundedCornerShape(Theme.radius.full))
                        .background(Theme.colorScheme.background.surfaceLow)
                        .clickable(onClick = onPlusClick)
                        .padding(Theme.spacing._4 + Theme.spacing._2)
                )
            }
        } else {
            Icon(
                painter = painterResource(Res.drawable.ic_shopping_bag_add),
                tint = Color(dukanColor),
                contentDescription = stringResource(Res.string.add_product),
                modifier = modifier.size(36.dp)
                    .clip(RoundedCornerShape(Theme.radius.md))
                    .border(
                        1.dp,
                        Theme.colorScheme.stroke,
                        RoundedCornerShape(Theme.radius.md)
                    )
                    .clickable(onClick = onAddClick, indication = null, interactionSource = null)
                    .padding(Theme.spacing._8)
            )
        }
    }
}

@Preview()
@Composable
private fun ProductActionNoImageDukanPreview() {
    MenaTheme {
        ProductActionNoImageDukan(
            inCartQuantity = 0,
            dukanColor = 0xFFFB5B5D,
            onAddClick = {},
            onPlusClick = {},
            onMinusClick = {}
        )
    }
}

@Preview()
@Composable
private fun ProductActionHasQuantityNoImageDukanPreview() {
    MenaTheme {
        ProductActionNoImageDukan(
            inCartQuantity = 1,
            dukanColor = 0xFFFB5B5D,
            onAddClick = {},
            onPlusClick = {},
            onMinusClick = {}
        )
    }
}