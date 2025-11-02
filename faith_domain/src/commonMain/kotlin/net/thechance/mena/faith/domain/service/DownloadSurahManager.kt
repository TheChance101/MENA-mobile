package net.thechance.mena.faith.domain.service

interface DownloadSurahManager {
    suspend fun downloadSurahFile(
        url: String,
        fileName: String,
    ): String
}
