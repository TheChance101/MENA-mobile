package net.thechance.mena.faith.data.remote.mapper.prayertime

import net.thechance.mena.faith.data.remote.dto.prayertime.PrayerTimesDto
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun PrayerTimesDto.toDomain(): List<PrayerTime> {
    val startOfDayTimeStamp: Long = this.date?.gregorian?.timestamp.toSafeLong()
    return listOf(
        PrayerTime(
            name = PrayerName.SUNRISE,
            time = sunrise.toInstant(startOfDayTimeStamp),
            hijriDate = date?.hijri?.readableDate.orEmpty()
        ),
        PrayerTime(
            name = PrayerName.FAJR,
            time = fajr.toInstant(startOfDayTimeStamp),
            hijriDate = date?.hijri?.readableDate.orEmpty()
        ),
        PrayerTime(
            name = PrayerName.DHUHR,
            time = dhuhr.toInstant(startOfDayTimeStamp),
            hijriDate = date?.hijri?.readableDate.orEmpty()
        ),
        PrayerTime(
            name = PrayerName.ASR,
            time = asr.toInstant(startOfDayTimeStamp),
            hijriDate = date?.hijri?.readableDate.orEmpty()
        ),
        PrayerTime(
            name = PrayerName.MAGHRIB,
            time = maghrib.toInstant(startOfDayTimeStamp),
            hijriDate = date?.hijri?.readableDate.orEmpty()
        ),
        PrayerTime(
            name = PrayerName.ISHA,
            time = isha.toInstant(startOfDayTimeStamp),
            hijriDate = date?.hijri?.readableDate.orEmpty()
        )
    )
}

private fun String?.toSafeLong(): Long = runCatching { this?.toLong() ?: 0L }.getOrDefault(0L)
