package net.thechance.mena.dukan.presentation.screen.CreateDukan.components

import androidx.compose.foundation.border
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
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun UploadImageContainer() {
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
    val borderColor = Theme.colorScheme.brand.brand
    Box(
        modifier =
            Modifier.fillMaxWidth()
                .height(156.dp)
                .clip(shape = RoundedCornerShape(Theme.radius.xl))
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(Theme.radius.xl),
                )
                .drawBehind {
                    drawRoundRect(
                        color = borderColor,
                        style = Stroke(width = 2.dp.toPx(), pathEffect = dashEffect),
                        cornerRadius = CornerRadius(12.dp.toPx())
                    )
                },
        contentAlignment = Alignment.Center
    )
    {
        Column {
            Text(
                text = "Click to upload",
                color = Theme.colorScheme.primary.primary,
                style = Theme.typography.label.medium,
            )
        }
    }
}

@Preview
@Composable
private fun UploadImageContainerPreview() {
    UploadImageContainer()
}
