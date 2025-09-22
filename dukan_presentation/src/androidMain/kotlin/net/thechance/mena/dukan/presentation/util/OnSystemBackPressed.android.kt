package net.thechance.mena.dukan.presentation.util

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun OnSystemBackPressed(onBack: () -> Unit) {
    BackHandler(enabled = true, onBack = onBack)
}