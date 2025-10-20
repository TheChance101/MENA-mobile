package net.thechance.mena.faith.data.remote.model.prayertime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrayerDateDto(
    @SerialName("hijri")
    val hijri: HijriDateDto? = null,
    @SerialName("gregorian")
    val gregorian: GregorianDateDto? = null
)
