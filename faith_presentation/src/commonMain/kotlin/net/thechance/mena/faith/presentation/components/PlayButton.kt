package net.thechance.mena.faith.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun PlayButton(
    painterIcon: Painter,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterIcon,
        contentDescription = contentDescription,
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surface)
            .padding(Theme.spacing._8)
    )
}
