package net.thechance.mena.faith.data.audio

import net.thechance.mena.faith.domain.mediaPlayer.QuranPlayer

expect class QuranPlayerImpl : QuranPlayer {
    override fun playAyah(ayahUrl: String)
    override fun pauseAyah()
    override fun repeatCurrentAyah()
    override fun onAyahCompleted(listener: () -> Unit)
}