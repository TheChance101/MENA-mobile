package net.thechance.mena.faith.data.remote.mapper.prayertime

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
interface StringHoursAndMinutesToInstantMapper {

    fun String?.toInstant(startOfDayTimeStamp: Long): Instant = runCatching {
        stringTimeToInstant(this, startOfDayTimeStamp)
    }.getOrDefault(Instant.fromEpochMilliseconds(startOfDayTimeStamp))

    private fun stringTimeToInstant(hoursAndMinutes: String?, startOfDayTimeStamp: Long): Instant {
        val parts = hoursAndMinutes?.split(":").orEmpty()
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        val startOfDayInstant = Instant.fromEpochSeconds(startOfDayTimeStamp)
        val localDate = startOfDayInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val dateTime = LocalDateTime(
            year = localDate.year,
            month = localDate.month.number,
            day = localDate.day,
            hour = hour,
            minute = minute
        )

        return dateTime.toInstant(TimeZone.currentSystemDefault())
    }
}
