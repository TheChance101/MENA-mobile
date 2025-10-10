package net.thechance.mena.core_chat.data.source.remote.imageDownloader

interface ImageDownloader {
    suspend fun downloadImageToGallery(url: String): Boolean
}

class ImageDownloaderImp: ImageDownloader {
    override suspend fun downloadImageToGallery(url: String): Boolean {
        return downloadImageToGalleryPlatform(url)
    }
}

expect suspend fun downloadImageToGalleryPlatform(url: String): Boolean