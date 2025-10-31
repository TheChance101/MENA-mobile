package net.thechance.mena.identity.data.repository

import net.thechance.mena.identity.data.dataSource.local.database.ImageCacheManager
import net.thechance.mena.identity.domain.repository.CachedImageRepository

class CachedImageRepositoryImpl(
    private val imageCacheManager: ImageCacheManager
): CachedImageRepository {
    override fun getCachedImage(key: String): ByteArray? {
        return imageCacheManager.getCachedImage(key)
    }

    override fun cacheImage(key: String, imageByteArray: ByteArray) {
        imageCacheManager.cacheImage(key,imageByteArray)
    }

    override fun removeCachedImage(key: String) {
        imageCacheManager.removeCachedImage(key)
    }
}