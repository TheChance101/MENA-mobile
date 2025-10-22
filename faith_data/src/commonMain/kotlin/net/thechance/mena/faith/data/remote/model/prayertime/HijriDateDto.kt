package net.thechance.mena.faith.data.remote.model.prayertime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HijriDateDto(
    @SerialName("date")
    val date: String? = null,
    @SerialName("dateFormat")
    val dateFormat: String? = null,
    @SerialName("readableDate")
    val readableDate: String? = null,
    @SerialName("day")
    val day: String? = null,
    @SerialName("dayName")
    val dayName: String? = null,
    @SerialName("dayArabicName")
    val dayArabicName: String? = null,
    @SerialName("month")
    val month: Int? = null,
    @SerialName("monthName")
    val monthName: String? = null,
    @SerialName("monthArabicName")
    val monthArabicName: String? = null,
    @SerialName("year")
    val year: String? = null
)
