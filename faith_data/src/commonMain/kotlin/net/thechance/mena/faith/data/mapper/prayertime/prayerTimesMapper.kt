package net.thechance.mena.faith.data.mapper.prayertime

import kotlinx.datetime.LocalDate
import net.thechance.mena.faith.data.database.prayertimes.PrayerTimesLocal
import net.thechance.mena.faith.data.remote.model.prayertime.PrayerTimesDto
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun PrayerTimesDto.toDomain(): List<PrayerTime> = generatePrayerTimesList(
    hijriDate = hijriDate.orEmpty(),
    fajrTime = fajr.toInstant(),
    sunriseTime = sunrise.toInstant(),
    dhuhrTime = dhuhr.toInstant(),
    asrTime = asr.toInstant(),
    maghribTime = maghrib.toInstant(),
    ishaTime = isha.toInstant()
)

@OptIn(ExperimentalTime::class)
fun PrayerTimesLocal.toDomain(): List<PrayerTime> = generatePrayerTimesList(
    hijriDate = hijriDate,
    fajrTime = fajr.toInstant(),
    sunriseTime = sunrise.toInstant(),
    dhuhrTime = dhuhr.toInstant(),
    asrTime = asr.toInstant(),
    maghribTime = maghrib.toInstant(),
    ishaTime = isha.toInstant()
)

fun PrayerTimesDto.toLocal(
    date: LocalDate,
    latitude: Double,
    longitude: Double
): PrayerTimesLocal = PrayerTimesLocal(
    latitude = latitude,
    longitude = longitude,
    date = date,
    hijriDate = hijriDate.orEmpty(),
    fajr = fajr.orEmpty(),
    sunrise = sunrise.orEmpty(),
    dhuhr = dhuhr.orEmpty(),
    asr = asr.orEmpty(),
    maghrib = maghrib.orEmpty(),
    isha = isha.orEmpty()
)

@OptIn(ExperimentalTime::class)
private fun generatePrayerTimesList(
    hijriDate: String,
    fajrTime: Instant,
    sunriseTime: Instant,
    dhuhrTime: Instant,
    asrTime: Instant,
    maghribTime: Instant,
    ishaTime: Instant
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

@OptIn(ExperimentalTime::class)
private fun String?.toInstant(): Instant = runCatching {
    Instant.parse(this.orEmpty())
}.getOrDefault(Instant.DISTANT_PAST)
