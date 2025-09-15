package net.thechance.mena.faith.presentation.designSystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.faith.presentation.designSystem.typography.LocalQuranTypography
import net.thechance.mena.faith.presentation.designSystem.typography.QuranTypology
import net.thechance.mena.faith.presentation.designSystem.typography.QuranTypology.Companion.getFontFamily
import net.thechance.mena.designsystem.presentation.theme.typography.Typography as MenaTypography

@Composable
fun QuranTheme(content: @Composable () -> Unit) {
    MenaTheme {
        val fontFamily = getFontFamily()
        val quranTypography = remember { QuranTypology.create(fontFamily) }
        CompositionLocalProvider(
            value = LocalQuranTypography provides quranTypography,
            content = content
        )
    }
}

val MenaTypography.quran: QuranTypology
    @Composable
    @ReadOnlyComposable
    get() = LocalQuranTypography.current
