package net.thechance.mena.wallet.presentation.utils

import org.koin.core.annotation.Single

interface FileManager {
    suspend fun saveFile(
        data: ByteArray,
        location: StorageLocation,
        mimeType: String
    ): String

    suspend fun readFile(location: StorageLocation): ByteArray

    suspend fun deleteFile(location: StorageLocation)

    suspend fun checkIfFileExists(location: StorageLocation): Boolean
}

 @Single
expect class FileManagerImpl() : FileManager{
    override suspend fun saveFile(
        data: ByteArray,
        location: StorageLocation,
        mimeType: String
    ): String

    override suspend fun readFile(location: StorageLocation): ByteArray
    override suspend fun deleteFile(location: StorageLocation)
    override suspend fun checkIfFileExists(location: StorageLocation): Boolean

}