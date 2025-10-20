package net.thechance.mena.core_chat.domain.service

interface ImageDownloaderService {
    suspend fun downloadImageToGallery(url: String): Boolean
}