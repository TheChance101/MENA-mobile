package net.thechance.mena.faith.presentation.feature.main

interface MainInteractionListener {
    fun onContinueTilawahClick(surahId: Int, surahName: String, ayahNumber: Int)
    fun onQuranClick()
    fun onQiblahClick()
    fun onMosquesClick()
    fun onLocationClick()
    fun onPrayerTimeClick()
    fun onTilawahClick()
}