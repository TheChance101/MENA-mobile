package net.thechance.mena.identity.data.dataSource.local.storage

expect class ImagesGalleryManager {
    suspend fun saveImage(imageBytes: ByteArray)
}