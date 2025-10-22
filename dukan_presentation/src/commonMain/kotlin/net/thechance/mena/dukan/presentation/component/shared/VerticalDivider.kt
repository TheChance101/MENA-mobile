package net.thechance.mena.dukan.presentation.component.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun VerticalDivider(
    color: Color,
    thickness: Dp,
    height: Dp = 20.dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(thickness)
            .height(height)
            .background(color)
    )
}
