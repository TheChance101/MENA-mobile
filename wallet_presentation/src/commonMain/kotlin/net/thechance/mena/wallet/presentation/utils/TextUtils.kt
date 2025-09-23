package net.thechance.mena.wallet.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.takeOrElse

fun TextUnit.toDp(density: Density) = with(density) { this@toDp.toPx().toDp() }