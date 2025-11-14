package net.thechance.mena.faith.presentation.designSystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import net.thechance.mena.designsystem.presentation.theme.typography.Typography
import net.thechance.mena.faith.presentation.designSystem.typography.LocalQuranTypography
import net.thechance.mena.faith.presentation.designSystem.typography.QuranTypography
import net.thechance.mena.faith.presentation.designSystem.typography.QuranTypography.Companion.getFontFamily

@Composable
internal fun QuranTheme(content: @Composable () -> Unit) {
        val fontFamily = getFontFamily()
        val quranTypography = remember { QuranTypography.create(fontFamily) }
        CompositionLocalProvider(
            value = LocalQuranTypography provides quranTypography,
            content = content
        )
}

internal val Typography.quran: QuranTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalQuranTypography.current
