package net.thechance.mena.faith.presentation.designSystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.faith.presentation.designSystem.typography.LocalQuranTypography
import net.thechance.mena.faith.presentation.designSystem.typography.QuranTextStyle
import net.thechance.mena.faith.presentation.designSystem.typography.createQuranTypography


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

object QuranTheme {
    val typography: QuranTextStyle
        @Composable @ReadOnlyComposable get() = LocalQuranTypography.current
}





