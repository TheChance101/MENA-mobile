package net.thechance.mena.designsystem.presentation.theme.radius

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Radius(
    val xxs: Dp,
    val xs: Dp,
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
    val xl: Dp,
    val full: Dp
)

val MenaRadius = Radius(
    xxs = 2.dp,
    xs = 4.dp,
    sm = 8.dp,
    md = 12.dp,
    lg = 16.dp,
    xl = 24.dp,
    full = 32.dp
)

val LocalRadius = staticCompositionLocalOf { MenaRadius }