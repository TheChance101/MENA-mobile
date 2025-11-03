package net.thechance.mena.faith.data.remote.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import net.thechance.mena.faith.data.utils.unpackZip
import net.thechance.mena.faith.domain.exception.FaithException
import net.thechance.mena.faith.domain.service.DownloadSurahManager
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM

class DownloadSurahManagerImpl : DownloadSurahManager {
    override suspend fun downloadSurahFile(
        url: String,
        fileName: String,
    ): String {
        return withContext(Dispatchers.IO) {
            val downloadedFilePath = downloadSurahFileToAppStorage(url, fileName)
            downloadedFilePath?.let {
                val unZippedFilePath = unZipFile(it)
                FileSystem.SYSTEM.delete(it.toPath())
                unZippedFilePath
            } ?: throw FaithException.FailedToDownloadSurahException
        }
    }

    private fun unZipFile(path: String): String =
        try {
            val zipPath = path.toPath()
            val destDir =
                zipPath.parent ?: throw IllegalStateException("Zip file has no parent directory")
            FileSystem.SYSTEM.unpackZip(zipPath, destDir)
            destDir.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
}

expect suspend fun downloadSurahFileToAppStorage(
    url: String,
    fileName: String,
): String?
