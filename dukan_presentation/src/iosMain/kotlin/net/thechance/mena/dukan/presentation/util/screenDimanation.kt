package net.thechance.mena.dukan.presentation.util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectGetHeight
import platform.CoreGraphics.CGRectGetWidth
import platform.UIKit.UIScreen


@OptIn(ExperimentalForeignApi::class)
actual fun getScreenWidth(): Dp {
    val bounds = UIScreen.mainScreen.bounds
    return CGRectGetWidth(bounds).dp
}

@OptIn(ExperimentalForeignApi::class)
actual fun getScreenHeight(): Dp {
    val bounds = UIScreen.mainScreen.bounds
    return CGRectGetHeight(bounds).dp
}
