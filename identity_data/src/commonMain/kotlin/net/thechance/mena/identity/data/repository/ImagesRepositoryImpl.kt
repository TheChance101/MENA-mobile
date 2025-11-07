package net.thechance.mena.identity.data.repository

import net.thechance.mena.identity.data.dataSource.local.memory.ImageCacheManager
import net.thechance.mena.identity.data.dataSource.local.storage.ImagesGalleryManager
import net.thechance.mena.identity.domain.repository.ImagesRepository

class ImagesRepositoryImpl(
    private val imageCacheManager: ImageCacheManager,
    private val imagesGalleryManager: ImagesGalleryManager
) : ImagesRepository {
    override fun getCachedImage(key: String): ByteArray? {
        return imageCacheManager.getCachedImage(key)
    }

    override fun cacheImage(key: String, imageByteArray: ByteArray) {
        imageCacheManager.cacheImage(key, imageByteArray)
    }

    override fun removeCachedImage(key: String) {
        imageCacheManager.removeCachedImage(key)
    }

    override suspend fun saveImageToGallery(imageByteArray: ByteArray) {
        imagesGalleryManager.saveImage(imageByteArray)
    }
}