package net.thechance.mena.wallet.presentation.utils

import kotlinx.serialization.Serializable

@Serializable
sealed class StorageLocation {
    data class Cache(val fileName: String) : StorageLocation()
    data class Downloads(val fileName: String) : StorageLocation()
}