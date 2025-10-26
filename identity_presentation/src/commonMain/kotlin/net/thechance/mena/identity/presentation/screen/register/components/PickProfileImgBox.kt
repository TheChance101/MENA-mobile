package net.thechance.mena.identity.presentation.screen.register.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.click_to_upload
import mena.identity_presentation.generated.resources.ic_add_image
import mena.identity_presentation.generated.resources.pencil_edit
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun UploadImageContainer(
    onClick: () -> Unit,
    image: ImageBitmap?,
    modifier: Modifier = Modifier,
) {
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f)
    val borderColor = Theme.colorScheme.brand.brand
    val radius = Theme.radius.xl

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f / 1f)
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(radius))
                .background(Theme.colorScheme.background.surfaceLow)
                .drawWithContent {
                    drawContent()
                    drawRoundRect(
                        color = borderColor,
                        style = Stroke(width = 2.dp.toPx(), pathEffect = dashEffect),
                        cornerRadius = CornerRadius(radius.toPx())
                    )
                }
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {

            if (image != null) {
                Image(
                    bitmap = image,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(Res.drawable.ic_add_image),
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(Res.string.click_to_upload),
                        color = Theme.colorScheme.primary.primary,
                        style = Theme.typography.label.medium
                    )
                }
            }
        }
        if (image != null) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = 20.dp)
                    .clip(shape = RoundedCornerShape(Theme.radius.full))
                    .background(Theme.colorScheme.primary.primary)
                    .border(
                        width = 1.dp,
                        color = Theme.colorScheme.background.surface,
                        shape = RoundedCornerShape(radius)
                    )
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.pencil_edit),
                    contentDescription = "Bottom Action",
                    tint = Theme.colorScheme.primary.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        UploadImageContainer(
            onClick = {},
            image = null,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun Preview2() {
    MenaTheme {
        UploadImageContainer(
            onClick = {},
            image = ImageBitmap(1, 1),
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}