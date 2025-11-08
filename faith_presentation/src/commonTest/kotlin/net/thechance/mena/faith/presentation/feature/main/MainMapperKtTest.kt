package net.thechance.mena.faith.presentation.feature.main

import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.asr
import mena.faith_presentation.generated.resources.dhuhr
import mena.faith_presentation.generated.resources.fajr
import mena.faith_presentation.generated.resources.isha
import mena.faith_presentation.generated.resources.maghrib
import mena.faith_presentation.generated.resources.sunrise
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class MainMapperKtTest {

    @Test
    fun `getPrayerDisplayNameResource should return correct string resource id for each prayer`() {
        val expectedMap = mapOf(
            PrayerName.FAJR to Res.string.fajr,
            PrayerName.DHUHR to Res.string.dhuhr,
            PrayerName.ASR to Res.string.asr,
            PrayerName.MAGHRIB to Res.string.maghrib,
            PrayerName.ISHA to Res.string.isha,
            PrayerName.SUNRISE to Res.string.sunrise
        )

        expectedMap.forEach { (prayer, expectedRes) ->
            assertEquals(expectedRes, getPrayerDisplayNameResource(prayer))
        }
    }

    @Test
    fun `isAM should return true for morning times`() {
        val morningInstant = Instant.fromEpochSeconds(MORNING_EIGHT_AM_SECONDS)

        val result = isAM(morningInstant)

        assertEquals(true, result)
    }

    @Test
    fun `isAM should return false for afternoon times`() {
        val afternoonInstant = Instant.fromEpochSeconds(15 * 3600)

        val result = isAM(afternoonInstant)

        assertEquals(false, result)
    }

    @Test
    fun `toUi should correctly map and filter and order prayers`() {
        val now = Instant.fromEpochSeconds(10_000)
        val prayers = listOf(
            PrayerTime(PrayerName.ISHA, now + 13.hours, DEFAULT_HIJRI_DATE),
            PrayerTime(PrayerName.FAJR, now, DEFAULT_HIJRI_DATE),
            PrayerTime(PrayerName.SUNRISE, now + 2.hours, DEFAULT_HIJRI_DATE),
            PrayerTime(PrayerName.DHUHR, now + 5.hours, DEFAULT_HIJRI_DATE),
            PrayerTime(PrayerName.ASR, now + 8.hours, DEFAULT_HIJRI_DATE),
            PrayerTime(PrayerName.MAGHRIB, now + 11.hours, DEFAULT_HIJRI_DATE)
        )

        val uiState = prayers.toUi(now + 6.hours)

        assertEquals(5, uiState.prayers.size)
        val expectedOrder = listOf(
            PrayerName.FAJR, PrayerName.DHUHR, PrayerName.ASR, PrayerName.MAGHRIB, PrayerName.ISHA
        )
        assertEquals(expectedOrder, uiState.prayers.map { it.name })
        assertNotNull(uiState.nextPrayerIndex)
        assertEquals(2, uiState.nextPrayerIndex)
    }

    @Test
    fun `getCurrentPrayer should return first prayer when now is before first OR after last prayer`() {
        val base = Instant.fromEpochSeconds(10_000)
        val prayers = listOf(
            PrayerTime(PrayerName.FAJR, base + 1.hours, DEFAULT_HIJRI_DATE),
            PrayerTime(PrayerName.DHUHR, base + 5.hours, DEFAULT_HIJRI_DATE),
            PrayerTime(PrayerName.ASR, base + 8.hours, DEFAULT_HIJRI_DATE),
            PrayerTime(PrayerName.MAGHRIB, base + 11.hours, DEFAULT_HIJRI_DATE),
            PrayerTime(PrayerName.ISHA, base + 13.hours, DEFAULT_HIJRI_DATE)
        )

        val beforeUi = prayers.toUi(base - 30.minutes)
        val afterUi = prayers.toUi(base + 15.hours)

        assertEquals(0, beforeUi.nextPrayerIndex)
        assertEquals(PrayerName.FAJR, beforeUi.prayers[0].name)
        assertEquals(0, afterUi.nextPrayerIndex)
        assertEquals(PrayerName.FAJR, afterUi.prayers[0].name)
    }

    @Test
    fun `getCurrentPrayer should move to next when now equals prayer time`() {
        val base = Instant.fromEpochSeconds(10_000)
        val prayers = listOf(
            PrayerTime(PrayerName.FAJR, base, DEFAULT_HIJRI_DATE),
            PrayerTime(PrayerName.DHUHR, base + 5.hours, DEFAULT_HIJRI_DATE),
            PrayerTime(PrayerName.ASR, base + 8.hours, DEFAULT_HIJRI_DATE)
        )

        val uiState = prayers.toUi(base)

        assertEquals(1, uiState.nextPrayerIndex)
        assertEquals(PrayerName.DHUHR, uiState.prayers[uiState.nextPrayerIndex].name)
    }

    @Test
    fun `toUi should handle missing prayers gracefully`() {
        val now = Instant.fromEpochSeconds(10_000)
        val prayers = listOf(
            PrayerTime(PrayerName.FAJR, now, DEFAULT_HIJRI_DATE),
            PrayerTime(PrayerName.DHUHR, now + 5.hours, DEFAULT_HIJRI_DATE),
            PrayerTime(PrayerName.ISHA, now + 13.hours, DEFAULT_HIJRI_DATE)
        )

        val uiState = prayers.toUi(now)

        assertEquals(3, uiState.prayers.size)
        assertNotNull(uiState.nextPrayerIndex)
    }

    companion object {
        private const val SECONDS_IN_HOUR: Long = 3600
        private const val MORNING_EIGHT_AM_SECONDS: Long = 8 * SECONDS_IN_HOUR
        private const val DEFAULT_HIJRI_DATE = "1446-01-01"

    }
}