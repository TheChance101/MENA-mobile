package net.thechance.mena.faith.data.remote.model.tilawah

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SurahSoundRequest(
    @SerialName("reciterId")
    val reciterId: Int,
    @SerialName("surahNumber")
    val surahNumber: Int?,
)
