package net.thechance.mena.faith.presentation.util.extentions

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun formatInstantToTimeString(instant: Instant): String {
    val instant = Instant.fromEpochMilliseconds(instant.toEpochMilliseconds())
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    val hour = localDateTime.hour.toString().padStart(2, '0')
    val minute = localDateTime.minute.toString().padStart(2, '0')

    return "$hour:$minute"
}