package net.thechance.mena.faith.presentation.feature.quran.bookmark

interface BookmarkInteractionListener {
    fun onBackClick()
    fun onDeleteBookmarkClick(id: Int)
    fun onStartTilawahClick()
}
