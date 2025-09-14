package net.thechance.mena.faith.presentation.designSystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.faith.presentation.designSystem.typography.LocalQuranTypography
import net.thechance.mena.faith.presentation.designSystem.typography.QuranTextStyle
import net.thechance.mena.faith.presentation.designSystem.typography.createQuranTypography
import net.thechance.mena.designsystem.presentation.theme.typography.Typography as MenaTypography

@Composable
fun QuranTheme(content: @Composable () -> Unit) {
    MenaTheme {
        val quranTypography: QuranTextStyle = createQuranTypography()
        CompositionLocalProvider(
            LocalQuranTypography provides quranTypography
        ) {
            content()
        }
    }
}

val MenaTypography.quran: QuranTextStyle
    @Composable @ReadOnlyComposable get() = LocalQuranTypography.current


