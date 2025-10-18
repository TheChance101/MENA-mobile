package net.thechance.mena.faith.presentation.feature.quran.surah

interface SurahInteractionListener {
    fun onBackClick()
    fun onDismissActionButtons()
    fun onShareClick(ayahContent: String)
    fun onBookmarkClick(ayahNumber: Int)
    fun onAyahLongPress(ayahContent: String, ayahIndex: Int)
    fun onSearchClick()
    fun onCopyClick(ayahContent: String)
    fun onInitialAyahScrolled()
    fun highlightAyah(ayahNumber: Int)
    fun onFirstVisibleAyahChanged(ayahNumber: Int)
}