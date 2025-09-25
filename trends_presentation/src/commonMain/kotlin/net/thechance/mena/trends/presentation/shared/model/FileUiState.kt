package net.thechance.mena.trends.presentation.shared.model

data class FileUiState(
    val name: String = "",
    val extension: String = "",
    val duration: String = "",
    val sizeInBytes: Long = 0L,
    val size: String = "",
    val bytes: ByteArray = ByteArray(0),
)