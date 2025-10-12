package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.preat.peekaboo.image.picker.toImageBitmap
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme


@Composable
fun ImageGridItem(
    imageUrl: Any,
    index: Int,
    onClick: (Int) -> Unit,
    overlayText: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable { onClick(index) }
            .clip(RoundedCornerShape(0.dp)),
        contentAlignment = Alignment.Center
    ) {
        when (imageUrl) {
            is String -> AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                colorFilter = if (overlayText != null)
                    ColorFilter.tint(Color.Black.copy(alpha = 0.7f), BlendMode.Darken)
                else null
            )

            is ByteArray -> {
                val bitmap = imageUrl.toImageBitmap()
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    colorFilter = if (overlayText != null)
                        ColorFilter.tint(Color.Black.copy(alpha = 0.7f), BlendMode.Darken)
                    else null
                )
            }
        }


        overlayText?.let {
            Text(
                text = it,
                color = Theme.colorScheme.primary.onPrimary,
                style = Theme.typography.label.large,
                modifier = Modifier.offset(x = (-4).dp)
            )
        }
    }
}
