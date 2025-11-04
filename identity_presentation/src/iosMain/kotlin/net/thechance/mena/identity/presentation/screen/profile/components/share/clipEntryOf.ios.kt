package net.thechance.mena.identity.presentation.screen.profile.components.share

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry

@OptIn(ExperimentalComposeUiApi::class)
actual fun clipEntryOf(text: String): ClipEntry {
    return ClipEntry.withPlainText(text)
}