package net.thechance.mena.faith.data.remote.model.prayertime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrayerTimesDto(
    @SerialName("hijriDate")
    val hijriDate: String? = "",
    @SerialName("fajr")
    val fajr: String? = "",
    @SerialName("sunrise")
    val sunrise: String? = "",
    @SerialName("dhuhr")
    val dhuhr: String? = "",
    @SerialName("asr")
    val asr: String? = "",
    @SerialName("maghrib")
    val maghrib: String? = "",
    @SerialName("isha")
    val isha: String? = ""
)
