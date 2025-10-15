package net.thechance.mena.faith.data.remote.dto.prayertime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrayerDateDto(
    @SerialName("hijri")
    val hijri: HijriDateDto?,
    @SerialName("gregorian")
    val gregorian: GregorianDateDto?
)
