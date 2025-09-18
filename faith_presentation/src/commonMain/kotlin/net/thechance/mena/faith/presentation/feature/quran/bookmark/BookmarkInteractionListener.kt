package net.thechance.mena.faith.presentation.feature.quran.bookmark

interface BookmarkInteractionListener {
    fun onBackClick()
    fun onRemoveBookmarkClick(bookmarkId: Int)
    fun onStartTilawahClick()
}
