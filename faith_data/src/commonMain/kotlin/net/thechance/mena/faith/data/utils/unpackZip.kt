package net.thechance.mena.faith.data.utils

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.openZip
import okio.use

fun FileSystem.unpackZip(
    zipFile: Path,
    destinationDir: Path,
) {
    fun Path.createParentDirectories() {
        this.parent?.let { parent ->
            createDirectories(parent)
        }
    }

    val zipFileSystem = openZip(zipFile)
    val paths = zipFileSystem
        .listRecursively("/".toPath())
        .filter { zipFileSystem.metadata(it).isRegularFile }
        .toList()

    paths.forEach { zipFilePath ->
        zipFileSystem.source(zipFilePath).buffer().use { source ->
            val relativeFilePath = zipFilePath.toString().trimStart('/')
            val fileToWrite = destinationDir.resolve(relativeFilePath)
            fileToWrite.createParentDirectories()
            sink(fileToWrite).buffer().use { sink ->
                sink.writeAll(source)
            }
        }
    }
}