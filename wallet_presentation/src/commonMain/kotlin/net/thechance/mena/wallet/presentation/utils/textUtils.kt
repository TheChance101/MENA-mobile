package net.thechance.mena.wallet.presentation.utils

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit

fun TextUnit.toDp(density: Density) = with(density) { this@toDp.toPx().toDp() }