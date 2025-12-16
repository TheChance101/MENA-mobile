package net.thechance.mena.identity.presentation.screen.profile.profileMainScreen.components.share.utils

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry

@OptIn(ExperimentalComposeUiApi::class)
actual fun clipEntryOf(text: String): ClipEntry {
    return ClipEntry.withPlainText(text)
}