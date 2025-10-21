package net.thechance.mena.faith.data.remote.model.prayertime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.thechance.mena.faith.data.remote.mapper.prayertime.StringHoursAndMinutesToInstantMapper

@Serializable
data class PrayerTimesDto(
    @SerialName("sunrise")
    val sunrise: String? = null,
    @SerialName("fajr")
    val fajr: String? = null,
    @SerialName("dhuhr")
    val dhuhr: String? = null,
    @SerialName("asr")
    val asr: String? = null,
    @SerialName("maghrib")
    val maghrib: String? = null,
    @SerialName("isha")
    val isha: String? = null,
    @SerialName("date")
    val date: PrayerDateDto? = null
) : StringHoursAndMinutesToInstantMapper
