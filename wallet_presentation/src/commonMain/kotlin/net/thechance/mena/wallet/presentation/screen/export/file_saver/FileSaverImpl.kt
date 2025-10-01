package net.thechance.mena.wallet.presentation.screen.export.file_saver

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.write
import org.koin.core.annotation.Single

@Single
class FileSaverImpl : FileSaver {
    override suspend fun saveFile(
        suggestedName: String,
        extension: String,
        bytes: ByteArray
    ): Boolean {
        val file = FileKit.openFileSaver(suggestedName = suggestedName, extension = extension)
        file?.write(bytes)
        return file != null
    }
}