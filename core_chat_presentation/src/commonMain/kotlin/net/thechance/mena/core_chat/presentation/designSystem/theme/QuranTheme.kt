package net.thechance.mena.core_chat.presentation.designSystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import net.thechance.mena.core_chat.presentation.designSystem.typography.LocalQuranTypography
import net.thechance.mena.core_chat.presentation.designSystem.typography.QuranTypography
import net.thechance.mena.core_chat.presentation.designSystem.typography.QuranTypography.Companion.getFontFamily
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.typography.Typography

@Composable
internal fun QuranTheme(content: @Composable () -> Unit) {
    MenaTheme {
        val fontFamily = getFontFamily()
        val quranTypography = remember { QuranTypography.create(fontFamily) }
        CompositionLocalProvider(
            LocalQuranTypography provides quranTypography,
            LocalLayoutDirection provides LayoutDirection.Rtl
        ) {
            content()
        }
    }
}

internal val Typography.quran: QuranTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalQuranTypography.current
