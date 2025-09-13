package net.thechance.mena.designsystem.presentation.component.icon

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun MenaIcon(
    painter: Painter,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
    contentDescription: String? = null,
) {
    Icon(
        painter = painter,
        tint = tint,
        contentDescription = contentDescription,
        modifier = modifier
    )
}