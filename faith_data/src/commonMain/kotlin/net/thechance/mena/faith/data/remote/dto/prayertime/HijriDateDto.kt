package net.thechance.mena.faith.data.remote.dto.prayertime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HijriDateDto(
    @SerialName("date")
    val date: String?,
    @SerialName("dateFormat")
    val dateFormat: String?,
    @SerialName("readableDate")
    val readableDate: String?,
    @SerialName("day")
    val day: String?,
    @SerialName("dayName")
    val dayName: String?,
    @SerialName("dayArabicName")
    val dayArabicName: String?,
    @SerialName("month")
    val month: Int?,
    @SerialName("monthName")
    val monthName: String?,
    @SerialName("monthArabicName")
    val monthArabicName: String?,
    @SerialName("year")
    val year: String?
)
