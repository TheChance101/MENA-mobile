package net.thechance.mena.faith.presentation.feature.quran.sur

interface SurInteractionListener {
    fun onSurahClick(id: Int,surahName: String)
    fun onBackClick()
    fun onBookmarkClick()
}
