package net.thechance.mena.faith.data.remote.network

import kotlinx.cinterop.*
import kotlinx.coroutines.*
import platform.Foundation.*
import platform.darwin.*
import kotlin.coroutines.resume
import cocoapods.SSZipArchive.SSZipArchive

@OptIn(ExperimentalForeignApi::class)
actual suspend fun downloadFileIntoPrivateStorage(
    url: String,
    fileName: String,
    isZipFile: Boolean,
): String? =
    suspendCancellableCoroutine { cont ->
        val nsUrl = NSURL.URLWithString(url)!!
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
                    fileManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask).first() as NSURL

                val zipFile = documentsDir.URLByAppendingPathComponent("downloaded.zip")!!
                data.writeToURL(zipFile, atomically = true)

                val extractDir = documentsDir.URLByAppendingPathComponent("extracted")!!.path!!
                fileManager.createDirectoryAtPath(
                    extractDir,
                    withIntermediateDirectories = true,
                    attributes = null,
                    error = null,
                )

                val success = SSZipArchive.unzipFileAtPath(zipFile.path!!, toDestination = extractDir)
                if (success) {
                    cont.resume(extractDir)
                } else {
                    cont.resumeWith(Result.failure(Exception("Failed to extract ZIP")))
                }
            }
        task.resume()
    }
