package net.thechance.mena.trends.presentation.shared.model

data class FileUiState(
    val filePath: String = "",
    val name: String = "",
    val size: Long = 0L,
    val sizeText: String = ""
)