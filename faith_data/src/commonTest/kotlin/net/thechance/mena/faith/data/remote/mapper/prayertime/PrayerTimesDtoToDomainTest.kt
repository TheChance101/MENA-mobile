package net.thechance.mena.faith.data.remote.mapper.prayertime

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import net.thechance.mena.faith.data.mapper.prayertime.toDomain
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
        val result = fakePrayerTimesDto.toDomain()

        assertThat(result).isEqualTo(fakePrayerTimes)
    }

    @Test
    fun `toDomain should return data with defaults when data has nulls`() = runTest {
        val result = fakePrayerTimesDtoWithNulls.toDomain()

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
        )
        val startOfDayInstant: Instant = Instant.DISTANT_PAST
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
