package net.thechance.mena.faith.presentation.feature.quran.bookmark

interface BookmarkInteractionListener {
    fun onBackClick()
    fun onDeleteBookmarkClick(bookmarkId: Int)
    fun onStartTilawahClick()
    fun onConfirmDeleteBookmarkClick()
    fun onDismissDeleteConfirmationDialog()

}
