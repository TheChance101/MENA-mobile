package net.thechance.mena.faith.data.remote.dto.prayertime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.thechance.mena.faith.data.remote.mapper.prayertime.StringHoursAndMinutesToInstantMapper

@Serializable
data class PrayerTimesDto(
    @SerialName("sunrise")
    val sunrise: String?,
    @SerialName("fajr")
    val fajr: String?,
    @SerialName("dhuhr")
    val dhuhr: String?,
    @SerialName("asr")
    val asr: String?,
    @SerialName("maghrib")
    val maghrib: String?,
    @SerialName("isha")
    val isha: String?,
    @SerialName("date")
    val date: PrayerDateDto?
) : StringHoursAndMinutesToInstantMapper
