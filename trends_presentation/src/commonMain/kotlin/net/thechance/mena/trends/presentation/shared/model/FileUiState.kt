package net.thechance.mena.trends.presentation.shared.model

data class FileUiState(
    val name: String = "",
    val extension: String = "",
    val mimeType: String = "",
    val sizeInBytes: Long = 0L,
    val sizeInMegaBytes: String = "",
    val bytes: ByteArray = ByteArray(0),
)