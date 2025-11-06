package net.thechance.mena.identity.domain.repository

interface ImagesRepository {
    fun getCachedImage(key: String): ByteArray?
    fun cacheImage(key:String,imageByteArray: ByteArray)
    fun removeCachedImage(key: String)
    suspend fun saveImageToGallery(imageByteArray: ByteArray)
}