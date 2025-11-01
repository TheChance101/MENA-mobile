package net.thechance.mena.faith.data.remote.network

import net.thechance.mena.faith.domain.service.DownloadManager

class DownloadManagerImpl : DownloadManager {
    override fun downloadFile(url: String) {
        TODO("Not yet implemented")
    }
}

expect suspend fun downloadFileIntoPrivateStorage(
    url: String,
    fileName: String,
    isZipFile: Boolean,
): String?
