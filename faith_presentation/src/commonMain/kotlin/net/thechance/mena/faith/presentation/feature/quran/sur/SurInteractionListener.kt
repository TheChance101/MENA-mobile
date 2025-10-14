package net.thechance.mena.faith.presentation.feature.quran.sur

interface SurInteractionListener {
    fun onSurahClick(surahId: Int, surahName: String)
    fun onBackClick()
    fun onBookmarkClick()
    fun onSearchClick()

}
