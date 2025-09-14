package net.thechance.mena.faith.presentation.designSystem.typography

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.hafs
import org.jetbrains.compose.resources.Font

@Composable
fun quranFontFamily(): FontFamily = FontFamily(
    Font(Res.font.hafs, weight = FontWeight.Normal)
)