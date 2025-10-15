package net.thechance.mena.core_chat.data.source.remote.network

interface ImageDownloader {
    suspend fun downloadImageToGallery(url: String): Boolean
}

class ImageDownloaderImp: ImageDownloader {
    override suspend fun downloadImageToGallery(url: String): Boolean {
        return downloadImageToGalleryPlatformSpecific(url)
    }
}

expect suspend fun downloadImageToGalleryPlatformSpecific(url: String): Boolean