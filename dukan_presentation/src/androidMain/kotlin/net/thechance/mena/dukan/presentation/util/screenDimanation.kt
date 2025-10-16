package net.thechance.mena.dukan.presentation.util

import android.content.res.Resources
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

actual fun getScreenWidth(): Dp {
    val configuration = Resources.getSystem().configuration
    return configuration.screenWidthDp.dp
}

actual fun getScreenHeight(): Dp {
    val configuration = Resources.getSystem().configuration
    return configuration.screenHeightDp.dp
}
