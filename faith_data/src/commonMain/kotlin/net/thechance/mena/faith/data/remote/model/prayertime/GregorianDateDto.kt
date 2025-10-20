package net.thechance.mena.faith.data.remote.model.prayertime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GregorianDateDto(
    @SerialName("date")
    val date: String? = null,
    @SerialName("dateFormat")
    val dateFormat: String? = null,
    @SerialName("timestamp")
    val timestamp: String? = null,
    @SerialName("readableDate")
    val readableDate: String? = null,
    @SerialName("day")
    val day: String? = null,
    @SerialName("dayName")
    val dayName: String? = null,
    @SerialName("month")
    val month: Int? = null,
    @SerialName("monthName")
    val monthName: String? = null,
    @SerialName("year")
    val year: String? = null
)
