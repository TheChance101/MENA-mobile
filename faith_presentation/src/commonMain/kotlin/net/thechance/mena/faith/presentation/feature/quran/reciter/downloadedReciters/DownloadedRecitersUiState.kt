package net.thechance.mena.faith.presentation.feature.quran.reciter.downloadedReciters

import net.thechance.mena.faith.domain.model.Reciter

data class DownloadedRecitersUiState(
    val surahId: Int? = null,
    val displayedReciters: List<DownloadedReciterItemUi> = emptyList(),
    val cachedReciters: List<DownloadedReciterItemUi> = emptyList(),
    val query: String = "",
    val queryHint: String = "",
    val selectedReciterId: Int? = null,
    val isSwipeable: Boolean = false,
    val isDeleteConfirmationDialogVisible: Boolean = false,
    val reciterIdToDelete: Int? = null,
)

data class DownloadedReciterItemUi(
    val id: Int,
    val name: String,
    val recitingType: String,
    val isDownloaded: Boolean,
)

fun Reciter.toUi(isDownloaded: Boolean) = DownloadedReciterItemUi(
    id = id,
    name = name,
    recitingType = tilawahType,
    isDownloaded = isDownloaded
)
