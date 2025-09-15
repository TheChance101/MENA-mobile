package net.thechance.mena.dukan.presentation.screen.CreateDukan.content.component

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
import mena.dukan_presentation.generated.resources.`add shopping basket`
import mena.dukan_presentation.generated.resources.ic_add_shopping_basket
import net.thechance.mena.designsystem.presentation.component.icon.MenaIcon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AddToCartIcon(
    addToCartBackgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Theme.radius.full))
            .background(addToCartBackgroundColor)
            .padding(3.dp),
        contentAlignment = Alignment.Center
    ) {
        MenaIcon(
            painter = painterResource(Res.drawable.ic_add_shopping_basket),
            tint = Theme.colorScheme.primary.onPrimary,
            contentDescription = stringResource(Res.string.`add shopping basket`),
            modifier = Modifier.size(10.dp)
        )
    }
}

@Preview
@Composable
private fun AddToCartPreview(){
    AddToCartIcon(
        addToCartBackgroundColor = Theme.colorScheme.background.surfaceHigh
    )
}