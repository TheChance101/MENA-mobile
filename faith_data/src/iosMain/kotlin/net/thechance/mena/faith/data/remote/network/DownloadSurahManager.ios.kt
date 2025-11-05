package net.thechance.mena.faith.data.remote.network

import kotlinx.cinterop.*
import kotlinx.coroutines.*
import net.thechance.mena.faith.domain.exception.FaithException
import platform.Foundation.*
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
actual suspend fun downloadSurahFileToAppStorage(
    url: String,
    fileName: String,
): String? =
    suspendCancellableCoroutine { cont ->
        val nsUrl = NSURL.URLWithString(url) ?: run {
            cont.resumeWith(Result.failure(FaithException.UrlCreationException))
            return@suspendCancellableCoroutine
        }
        val session = NSURLSession.sharedSession
        val task =
            session.dataTaskWithURL(nsUrl) { data, _, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(Exception(error.localizedDescription)))
                    return@dataTaskWithURL
                }

                if (data == null) {
                    cont.resumeWith(Result.failure(Exception("No data received")))
                    return@dataTaskWithURL
                }

                val fileManager = NSFileManager.defaultManager
                val documentsDir = fileManager
                    .URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
                    .first() as NSURL

                val fileUrl = documentsDir.URLByAppendingPathComponent("$fileName.zip") ?: run {
                    cont.resumeWith(Result.failure(FaithException.UrlCreationException))
                    return@dataTaskWithURL
                }

                fileUrl.URLByDeletingLastPathComponent?.let { parentDir ->
                    fileManager.createDirectoryAtURL(
                        parentDir,
                        withIntermediateDirectories = true,
                        attributes = null,
                        error = null
                    )
                }

                val success = data.writeToURL(fileUrl, atomically = true)

                if (success) {
                    cont.resume(fileUrl.path)
                } else {
                    cont.resumeWith(Result.failure(Exception("Failed to write file")))
                }
            }
        task.resume()
    }
