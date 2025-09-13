package net.thechance.mena.dukan.presentation.screen.CreateDukan.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_add_image
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun UploadImageContainer(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f)
    val borderColor = Theme.colorScheme.brand.brand
    val radius = Theme.radius.xl

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(156.dp)
            .clip(RoundedCornerShape(radius))
            .drawBehind {
                drawRoundRect(
                    color = borderColor,
                    style = Stroke(width = 2.dp.toPx(), pathEffect = dashEffect),
                    cornerRadius = CornerRadius(radius.toPx())
                )
            }
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter =
                    painterResource(Res.drawable.ic_add_image),
                contentDescription = "Upload The image"
            )
            Text(
                text = "Click to upload",
                color = Theme.colorScheme.primary.primary,
                style = Theme.typography.label.medium
            )
        }
    }
}


@Preview
@Composable
private fun UploadImageContainerPreview() {
    MenaTheme {
        UploadImageContainer {
        }
    }
}