package net.thechance.mena.faith.presentation.feature.quran.surah.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    height: Dp = 20.dp,
    color: Color = Theme.colorScheme.stroke,
    thickness: Dp = 1.dp,
) {

    Box(
        modifier = modifier
            .width(thickness)
            .height(height)
            .background(color)

    )
}
