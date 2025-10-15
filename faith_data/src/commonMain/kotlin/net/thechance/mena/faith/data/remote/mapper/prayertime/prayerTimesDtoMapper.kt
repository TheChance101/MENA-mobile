package net.thechance.mena.faith.data.remote.mapper.prayertime

import kotlinx.datetime.TimeZone
import net.thechance.mena.faith.data.remote.dto.prayertime.PrayerTimesDto
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun PrayerTimesDto.toDomain(timeZone: TimeZone = TimeZone.currentSystemDefault()): List<PrayerTime> {
    val startOfDayTimeStamp: Long = this.date?.gregorian?.timestamp.toSafeLong()
    val hijriReadableDate = this.date?.hijri?.readableDate.orEmpty()
    return listOf(
        PrayerTime(
            name = PrayerName.SUNRISE,
            time = sunrise.toInstant(startOfDayTimeStamp, timeZone),
            hijriDate = hijriReadableDate
        ),
        PrayerTime(
            name = PrayerName.FAJR,
            time = fajr.toInstant(startOfDayTimeStamp, timeZone),
            hijriDate = hijriReadableDate
        ),
        PrayerTime(
            name = PrayerName.DHUHR,
            time = dhuhr.toInstant(startOfDayTimeStamp, timeZone),
            hijriDate = hijriReadableDate
        ),
        PrayerTime(
            name = PrayerName.ASR,
            time = asr.toInstant(startOfDayTimeStamp, timeZone),
            hijriDate = hijriReadableDate
        ),
        PrayerTime(
            name = PrayerName.MAGHRIB,
            time = maghrib.toInstant(startOfDayTimeStamp, timeZone),
            hijriDate = hijriReadableDate
        ),
        PrayerTime(
            name = PrayerName.ISHA,
            time = isha.toInstant(startOfDayTimeStamp, timeZone),
            hijriDate = hijriReadableDate
        )
    )
}

private fun String?.toSafeLong(): Long = runCatching { this?.toLong() ?: 0L }.getOrDefault(0L)
