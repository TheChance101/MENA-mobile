package net.thechance.mena.faith.data.repository

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import net.thechance.mena.faith.data.remote.model.prayertime.PrayerTimesDto
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun getPrayerTimesFakeData(
    timeZone: TimeZone = TimeZone.of("Africa/Cairo"),
    sunriseTime: Instant = LocalDateTime(
        year = 2025,
        month = 10,
        day = 10,
        hour = 6,
        minute = 52
    ).toInstant(timeZone),
    fajrTime: Instant = LocalDateTime(
        year = 2025,
        month = 10,
        day = 10,
        hour = 5,
        minute = 25
    ).toInstant(timeZone),
    dhuhrTime: Instant = LocalDateTime(
        year = 2025,
        month = 10,
        day = 10,
        hour = 12,
        minute = 40
    ).toInstant(timeZone),
    asrTime: Instant = LocalDateTime(
        year = 2025,
        month = 10,
        day = 10,
        hour = 15,
        minute = 59
    ).toInstant(timeZone),
    maghribTime: Instant = LocalDateTime(
        year = 2025,
        month = 10,
        day = 10,
        hour = 18,
        minute = 27
    ).toInstant(timeZone),
    ishaTime: Instant = LocalDateTime(
        year = 2025,
        month = 10,
        day = 10,
        hour = 19,
        minute = 45
    ).toInstant(timeZone),
    hijriDate: String = fakePrayerTimesDto.hijriDate.toString()
): List<PrayerTime> {
    return listOf(
        PrayerTime(
            name = PrayerName.FAJR,
            time = fajrTime,
            hijriDate = hijriDate
        ),
        PrayerTime(
            name = PrayerName.SUNRISE,
            time = sunriseTime,
            hijriDate = hijriDate
        ),
        PrayerTime(
            name = PrayerName.DHUHR,
            time = dhuhrTime,
            hijriDate = hijriDate
        ),
        PrayerTime(
            name = PrayerName.ASR,
            time = asrTime,
            hijriDate = hijriDate
        ),
        PrayerTime(
            name = PrayerName.MAGHRIB,
            time = maghribTime,
            hijriDate = hijriDate
        ),
        PrayerTime(
            name = PrayerName.ISHA,
            time = ishaTime,
            hijriDate = hijriDate
        )
    )
}

private val fakePrayerTimesDto = getFakePrayerTimesDto()

fun getFakePrayerTimesDto(
    sunrise: String? = "2025-10-10T03:52:00Z",
    fajr: String? = "2025-10-10T02:25:00Z",
    dhuhr: String? = "2025-10-10T09:40:00Z",
    asr: String? = "2025-10-10T12:59:00Z",
    maghrib: String? = "2025-10-10T15:27:00Z",
    isha: String? = "2025-10-10T16:45:00Z",
    hijriDate: String? = "18-04-1447",
): PrayerTimesDto = PrayerTimesDto(
    hijriDate = hijriDate,
    fajr = fajr,
    sunrise = sunrise,
    dhuhr = dhuhr,
    asr = asr,
    maghrib = maghrib,
    isha = isha,
)
