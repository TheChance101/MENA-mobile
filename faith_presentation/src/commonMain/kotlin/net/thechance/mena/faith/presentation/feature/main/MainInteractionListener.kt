package net.thechance.mena.faith.presentation.feature.main

interface FeatureInteractionListener {
    fun onQuranClick()
    fun onQiblahClick()
    fun onMosquesClick()
}


interface MainInteractionListener : FeatureInteractionListener {
    fun onContinueTilawahClick(surahId: Int, surahName: String, ayahNumber: Int)
}