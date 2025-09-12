package net.thechance.mena.designsystem.presentation.component.image

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale

@Composable
fun MenaImage(
    painter: Painter,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit
) {
    Image(
        painter = painter,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier
    )
}