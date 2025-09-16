package net.thechance.mena

import androidx.compose.runtime.Composable
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.faith.presentation.feature.quran.bookmark.BookmarkScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MenaTheme {
        BookmarkScreen()
    }
}
