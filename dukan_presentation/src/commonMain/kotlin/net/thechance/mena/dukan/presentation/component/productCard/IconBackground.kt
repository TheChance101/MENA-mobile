package net.thechance.mena.dukan.presentation.component.productCard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.edit_product_pencil
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun IconBackground(
    drawableResource: DrawableResource,
    contentDescription: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    boxModifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier
) {
    Box(
        modifier = boxModifier.clickable(onClick = onClick)
            .background(
            color = backgroundColor,
            shape = RoundedCornerShape(size = Theme.radius.full)
        )
    ) {
        Icon(
            painter = painterResource(drawableResource),
            contentDescription = contentDescription,
            modifier = iconModifier
        )
    }
}

@Preview
@Composable
private fun IconBackgroundPreview(){
    IconBackground(
        drawableResource = Res.drawable.edit_product_pencil,
        contentDescription = stringResource(Res.string.edit_product_pencil),
        backgroundColor = Theme.colorScheme.primary.primary,
        onClick = {},
        iconModifier = Modifier.padding(8.dp),
    )
}