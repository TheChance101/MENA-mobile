package net.thechance.mena.faith.data.remote.model.prayertime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrayerTimesDto(
    @SerialName("hijriDate")
    val hijriDate: String? = null,
    @SerialName("fajr")
    val fajr: String? = null,
    @SerialName("sunrise")
    val sunrise: String? = null,
    @SerialName("dhuhr")
    val dhuhr: String? = null,
    @SerialName("asr")
    val asr: String? = null,
    @SerialName("maghrib")
    val maghrib: String? = null,
    @SerialName("isha")
    val isha: String? = null
)
