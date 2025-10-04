package net.thechance.mena.dukan.presentation.util.file

import androidx.compose.ui.graphics.ImageBitmap
import com.attafitamim.krop.core.images.ImageSrc
import com.attafitamim.krop.filekit.toImageSrc
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.compose.util.toImageBitmap
import io.github.vinceglb.filekit.size

interface ImageFile {
    suspend fun size(): Long
    suspend fun toImageBitmap(): ImageBitmap
    suspend fun toImageSrc(): ImageSrc?
}

class PlatformImageFile(private val file: PlatformFile) : ImageFile {
    override suspend fun size(): Long = file.size()
    override suspend fun toImageBitmap(): ImageBitmap = file.toImageBitmap()
    override suspend fun toImageSrc(): ImageSrc? = file.toImageSrc()
}