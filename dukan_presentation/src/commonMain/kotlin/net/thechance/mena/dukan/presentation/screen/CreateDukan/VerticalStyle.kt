package net.thechance.mena.dukan.presentation.screen.createDukan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_add_shopping_basket
import mena.dukan_presentation.generated.resources.ic_image
import net.thechance.mena.designsystem.presentation.component.icon.MenaIcon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun VerticalStyle(
    modifier: Modifier = Modifier,
    addToCartColor: Color = Theme.colorScheme.primary.onPrimary
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Theme.radius.xs))
            .background(Theme.colorScheme.background.surface)
            .padding(top = 2.dp, bottom = 20.dp, start = 2.dp, end = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Theme.radius.xxs))
                .background(Theme.colorScheme.background.surfaceLow)
                .padding(9.dp),
            contentAlignment = Alignment.Center
        ) {
            MenaIcon(
                painter = painterResource(Res.drawable.ic_image),
                tint = Theme.colorScheme.stroke,
                contentDescription = "style has image",
                modifier = Modifier.size(24.dp)
            )
        }
        Box(
            modifier = Modifier
                .padding(top = 34.dp)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(Theme.radius.full))
                .background(Theme.colorScheme.background.surfaceHigh)
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            MenaIcon(
                painter = painterResource(Res.drawable.ic_add_shopping_basket),
                tint = addToCartColor,
                contentDescription = "add shopping basket",
                modifier = Modifier.size(10.dp)
            )
        }
    }
}

@Preview
@Composable
private fun VerticalStylePreview() {
    MenaTheme {
        VerticalStyle()
    }
}