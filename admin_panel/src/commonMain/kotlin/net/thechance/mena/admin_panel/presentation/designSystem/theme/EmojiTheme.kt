package net.thechance.mena.admin_panel.presentation.designSystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import net.thechance.mena.admin_panel.presentation.designSystem.typography.EmojiTypography
import net.thechance.mena.admin_panel.presentation.designSystem.typography.EmojiTypography.Companion.getFontFamily
import net.thechance.mena.admin_panel.presentation.designSystem.typography.LocalEmojiTypography
import net.thechance.mena.designsystem.presentation.theme.typography.Typography

@Composable
fun EmojiTheme(content: @Composable () -> Unit) {
    val fontFamily = getFontFamily()
    val emojiTypography = remember { EmojiTypography.Companion.create(fontFamily) }
    CompositionLocalProvider(
        value = LocalEmojiTypography provides emojiTypography,
        content = content
    )
}

val Typography.emoji: EmojiTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalEmojiTypography.current
