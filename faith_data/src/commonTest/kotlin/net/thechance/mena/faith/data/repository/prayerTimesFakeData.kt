package net.thechance.mena.faith.data.repository

import net.thechance.mena.faith.data.remote.dto.prayertime.GregorianDateDto
import net.thechance.mena.faith.data.remote.dto.prayertime.HijriDateDto
import net.thechance.mena.faith.data.remote.dto.prayertime.PrayerDateDto
import net.thechance.mena.faith.data.remote.dto.prayertime.PrayerTimesDto
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun getPrayerTimesFakeData(
    sunriseTime: Instant = Instant.parse("2025-10-10T03:52:00Z"), //10-10-2025 06:52
    fajrTime: Instant = Instant.parse("2025-10-10T02:25:00Z"), //10-10-2025 05:25
    dhuhrTime: Instant = Instant.parse("2025-10-10T09:40:00Z"), //10-10-2025 12:40
    asrTime: Instant = Instant.parse("2025-10-10T12:59:00Z"), //10-10-2025 15:59
    maghribTime: Instant = Instant.parse("2025-10-10T15:27:00Z"), //10-10-2025 18:27
    ishaTime: Instant = Instant.parse("2025-10-10T16:45:00Z"), //10-10-2025 19:45
    hijriDate: String = fakePrayerTimesDto.date?.hijri?.readableDate.orEmpty()
): List<PrayerTime> {
    return listOf(
        PrayerTime(
            name = PrayerName.SUNRISE,
            time = sunriseTime,
            hijriDate = hijriDate
        ),
        PrayerTime(
            name = PrayerName.FAJR,
            time = fajrTime,
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
    sunrise: String? = "06:52",
    fajr: String? = "05:25",
    dhuhr: String? = "12:40",
    asr: String? = "15:59",
    maghrib: String? = "18:27",
    isha: String? = "19:45",
    hijriDate: String? = "18-04-1447",
    hijriDateFormat: String? = "dd-MM-YYYY",
    hijriReadableDate: String? = "18 Rabīʿ al-thānī 1447",
    hijriDay: String? = "18",
    hijriDayName: String? = "Al Juma'a",
    hijriDayArabicName: String? = "الجمعة",
    hijriMonth: Int? = 4,
    hijriMonthName: String? = "Rabīʿ al-thānī",
    hijriMonthArabicName: String? = "رَبيع الثاني",
    hijriYear: String? = "1447",
    gregorianDate: String? = "18-04-1447",
    gregorianDateFormat: String? = "dd-MM-YYYY",
    timestamp: String? = "1760068800",// 10-10-2025
    gregorianReadableDate: String? = "18 Rabīʿ al-thānī 1447",
    gregorianDay: String? = "18",
    gregorianDayName: String? = "Al Juma'a",
    gregorianMonth: Int? = 4,
    gregorianMonthName: String? = "Rabīʿ al-thānī",
    gregorianYear: String? = "1447"

): PrayerTimesDto = PrayerTimesDto(
    sunrise = sunrise,
    fajr = fajr,
    dhuhr = dhuhr,
    asr = asr,
    maghrib = maghrib,
    isha = isha,
    date = PrayerDateDto(
        hijri = HijriDateDto(
            date = hijriDate,
            dateFormat = hijriDateFormat,
            readableDate = hijriReadableDate,
            day = hijriDay,
            dayName = hijriDayName,
            dayArabicName = hijriDayArabicName,
            month = hijriMonth,
            monthName = hijriMonthName,
            monthArabicName = hijriMonthArabicName,
            year = hijriYear
        ),
        gregorian = GregorianDateDto(
            date = gregorianDate,
            dateFormat = gregorianDateFormat,
            timestamp = timestamp,
            readableDate = gregorianReadableDate,
            day = gregorianDay,
            dayName = gregorianDayName,
            month = gregorianMonth,
            monthName = gregorianMonthName,
            year = gregorianYear,
        )
    )
)
