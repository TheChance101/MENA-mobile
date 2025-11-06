package net.thechance.mena.faith.presentation.utils.extentions.prayerTime

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun Instant.formatInstantToTimeString(withISPM: Boolean): String {
    val localDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())

    val hour24 = localDateTime.hour
    val hour12 = if (hour24 % 12 == 0) 12 else hour24 % 12
    val minute = localDateTime.minute.toString().padStart(2, '0')
    val amPm = if (hour24 < 12) "AM" else "PM"

    val hourString = hour12.toString().padStart(2, '0')
    return if (withISPM) "$hour12:$minute $amPm" else "$hourString:$minute"
}
