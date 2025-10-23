package net.thechance.mena.core_chat.data.source.remote.network

import net.thechance.mena.core_chat.domain.service.ImageDownloaderService


class ImageDownloaderImp: ImageDownloaderService {
    override suspend fun downloadImageToGallery(url: String): Boolean {
        return downloadImageToGalleryPlatformSpecific(url)
    }
}

expect suspend fun downloadImageToGalleryPlatformSpecific(url: String): Boolean