package net.thechance.mena.faith.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.quran

@Composable
fun getAyahTextStyle() = Theme.typography.quran.large.copy(
    textDirection = TextDirection.Rtl,
    textAlign = TextAlign.Justify
)