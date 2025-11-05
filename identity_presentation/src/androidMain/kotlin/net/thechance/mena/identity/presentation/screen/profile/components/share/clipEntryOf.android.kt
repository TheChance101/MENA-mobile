package net.thechance.mena.identity.presentation.screen.profile.components.share

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry

actual fun clipEntryOf(text: String): ClipEntry {
    return ClipEntry(
        clipData =  ClipData.newPlainText("text", text)
    )
}