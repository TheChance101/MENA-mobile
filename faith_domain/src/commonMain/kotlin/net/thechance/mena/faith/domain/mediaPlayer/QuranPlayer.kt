package net.thechance.mena.faith.domain.mediaPlayer

interface QuranPlayer {
    fun playAyah(
        ayahUrl: String,
    )

    fun playAyah(
        ayahUrl: String,
        surahName: String,
        ayahNumber: Int,
        reciterName: String,
    )

    fun pauseAyah()
    fun repeatCurrentAyah()
    fun onAyahCompleted(listener: () -> Unit)
}