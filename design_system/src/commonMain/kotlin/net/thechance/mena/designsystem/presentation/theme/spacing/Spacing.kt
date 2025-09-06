package net.thechance.mena.designsystem.presentation.theme.spacing

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val _2: Dp = 2.dp,
    val _4: Dp = 4.dp,
    val _8: Dp = 8.dp,
    val _12: Dp = 12.dp,
    val _16: Dp = 16.dp,
    val _24: Dp = 24.dp,
    val _32: Dp = 32.dp
)

val MenaSpacing = Spacing()
val LocalSpacing = staticCompositionLocalOf { MenaSpacing }