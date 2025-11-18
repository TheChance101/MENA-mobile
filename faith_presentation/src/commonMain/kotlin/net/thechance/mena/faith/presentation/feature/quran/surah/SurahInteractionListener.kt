package net.thechance.mena.faith.presentation.feature.quran.surah

interface SurahInteractionListener {
    fun onBackClick()
    fun onListenClick()
    fun onReciterClick(surahId: Int)
    fun onNextAyahClick()
    fun onPlayPauseClick()
    fun onRepeatAyahClick()
    fun onClosePlayerClick()
    fun onPreviousAyahClick()
    fun onDismissActionButtons()
    fun onShareClick(content: String)
    fun onBookmarkClick(ayahNumber: Int)
    fun onAyahLongPress(ayahContent: String, ayahIndex: Int)
    fun onSearchClick()
    fun onCopyClick(ayahContent: String)
    fun onInitialAyahScrolled()
    fun highlightAyah(ayahNumber: Int)
    fun updateContinueTilawah(ayahNumber: Int)
    fun playSurah(surahNumber: Int)
    fun onConfigrationChange()
}