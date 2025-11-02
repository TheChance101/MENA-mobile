package net.thechance.mena.faith.data.remote.network

import kotlinx.cinterop.*
import kotlinx.coroutines.*
import platform.Foundation.*
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
actual suspend fun downloadSurahFileToAppStorage(
    url: String,
    fileName: String,
): String? =
    suspendCancellableCoroutine { cont ->
        val nsUrl = NSURL.URLWithString(url) ?: throw Exception()
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
                val documentsDir =
                    fileManager
                        .URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
                        .first() as NSURL

                val zipFile =
                    documentsDir.URLByAppendingPathComponent("$fileName.zip") ?: throw Exception()
                data.writeToURL(zipFile, atomically = true)

                cont.resume(zipFile.path)
            }
        task.resume()
    }
