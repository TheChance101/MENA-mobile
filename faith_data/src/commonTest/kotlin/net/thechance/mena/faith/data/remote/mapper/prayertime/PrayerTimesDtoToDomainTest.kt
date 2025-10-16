package net.thechance.mena.faith.data.remote.mapper.prayertime

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import net.thechance.mena.faith.data.repository.getFakePrayerTimesDto
import net.thechance.mena.faith.data.repository.getPrayerTimesFakeData
import net.thechance.mena.faith.domain.entity.PrayerTime
import kotlin.test.Test
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class PrayerTimesDtoToDomainTest {

    @Test
    fun `toDomain should return list of PrayerTime when PrayerTimesDto is valid`() = runTest {
        //When
        val result = fakePrayerTimesDto.toDomain(timeZone)
        //Then
        assertThat(result).isEqualTo(fakePrayerTimes)
    }

    @Test
    fun `toDomain should return data with defaults when data has nulls`() = runTest {
        //When
        val result = fakePrayerTimesDtoWithNulls.toDomain(timeZone)
        //Then
        assertThat(result).isEqualTo(fakePrayerTimesWithDefaults)

    }

    private companion object {
        val timeZone = TimeZone.of("Africa/Cairo")
        val fakePrayerTimesDto = getFakePrayerTimesDto()
        val fakePrayerTimes: List<PrayerTime> = getPrayerTimesFakeData(timeZone)
        val fakePrayerTimesDtoWithNulls = getFakePrayerTimesDto(
            sunrise = null,
            fajr = null,
            dhuhr = null,
            asr = null,
            maghrib = null,
            isha = null,
            hijriDate = null,
            hijriDateFormat = null,
            hijriReadableDate = null,
            hijriDay = null,
            hijriDayName = null,
            hijriDayArabicName = null,
            hijriMonth = null,
            hijriMonthName = null,
            hijriMonthArabicName = null,
            hijriYear = null,
            gregorianDate = null,
            gregorianDateFormat = null,
            timestamp = null,
            gregorianReadableDate = null,
            gregorianDay = null,
            gregorianDayName = null,
            gregorianMonth = null,
            gregorianMonthName = null,
            gregorianYear = null
        )
        val startOfDayInstant: Instant = Instant.fromEpochMilliseconds(0)
        val fakePrayerTimesWithDefaults: List<PrayerTime> = getPrayerTimesFakeData(
            sunriseTime = startOfDayInstant,
            fajrTime = startOfDayInstant,
            dhuhrTime = startOfDayInstant,
            asrTime = startOfDayInstant,
            maghribTime = startOfDayInstant,
            ishaTime = startOfDayInstant,
            hijriDate = ""
        )
    }
}
