package net.thechance.mena.identity.data.dataSource.local.memory

expect class ImageCacheManager {

    fun getCachedImage(key: String): ByteArray?

    fun cacheImage(
        key: String,
        imageByteArray: ByteArray
    )

    fun removeCachedImage(key: String)
}