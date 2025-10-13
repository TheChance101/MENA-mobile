package net.thechance.mena.trends.presentation.shared.util

import io.github.vinceglb.filekit.PlatformFile

actual fun PlatformFile.getFilePath(): String {
    return this.nsUrl.toString()
}