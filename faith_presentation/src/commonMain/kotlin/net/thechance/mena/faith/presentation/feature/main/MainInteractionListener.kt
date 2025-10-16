package net.thechance.mena.faith.presentation.feature.main

interface MainInteractionListener {
    fun onContinueTilawahClick(surahId: Int, surahName: String)
    fun onQuranClick()
    fun onQiblahClick()
    fun onMosquesClick()
}
