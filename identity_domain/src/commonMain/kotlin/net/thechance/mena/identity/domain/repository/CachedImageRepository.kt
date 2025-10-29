package net.thechance.mena.identity.domain.repository

interface CachedImageRepository {
    fun getCachedImage(key: String): ByteArray?
    fun cacheImage(key:String,imageByteArray: ByteArray)
    fun removeCachedImage(key: String)

}