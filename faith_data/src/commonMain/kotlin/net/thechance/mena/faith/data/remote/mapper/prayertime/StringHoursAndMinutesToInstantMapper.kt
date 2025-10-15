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

    fun String?.toInstant(
        startOfDayTimeStamp: Long,
        timeZone: TimeZone = TimeZone.currentSystemDefault()
    ): Instant = runCatching {
        stringTimeToInstant(
            hoursAndMinutes = this,
            startOfDayTimeStamp = startOfDayTimeStamp,
            timeZone = timeZone
        )
    }.getOrDefault(Instant.fromEpochMilliseconds(startOfDayTimeStamp))

    private fun stringTimeToInstant(
        hoursAndMinutes: String?,
        startOfDayTimeStamp: Long,
        timeZone: TimeZone
    ): Instant {
        val parts = hoursAndMinutes?.split(":").orEmpty()
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        val startOfDayInstant = Instant.fromEpochSeconds(startOfDayTimeStamp)
        val localDate = startOfDayInstant.toLocalDateTime(timeZone).date
        val dateTime = LocalDateTime(
            year = localDate.year,
            month = localDate.month.number,
            day = localDate.day,
            hour = hour,
            minute = minute
        )

        return dateTime.toInstant(timeZone = timeZone)
    }
}
