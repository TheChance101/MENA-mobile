package net.thechance.mena.faith.domain.mediaPlayer

interface QuranPlayer {
    fun playAyah(ayahUrl: String)
    fun pauseAyah()
    fun repeatCurrentAyah()
}