package net.thechance.mena.faith.data.remote.model.tilawah

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AyahSoundUrlRequest(
    @SerialName("reciterId")
    val reciterId: Int,
    @SerialName("ayahNumber")
    val ayahNumber: Int,
    @SerialName("surahNumber")
    val surahNumber: Int
)
