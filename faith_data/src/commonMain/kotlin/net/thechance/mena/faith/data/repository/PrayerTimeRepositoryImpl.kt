package net.thechance.mena.faith.data.repository

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import net.thechance.mena.faith.data.mapper.prayertime.toDomain
import net.thechance.mena.faith.data.remote.model.prayertime.PrayerTimesDto
import net.thechance.mena.faith.data.remote.service.PrayerTimeApiService
import net.thechance.mena.faith.data.utils.executeApiSafely
import net.thechance.mena.faith.domain.entity.Location
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import net.thechance.mena.faith.domain.repository.PrayerTimeRepository
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class PrayerTimeRepositoryImpl(
    private val prayerTimeApiService: PrayerTimeApiService
) : PrayerTimeRepository {

    override suspend fun getPrayerTimes(
        date: Instant,
        location: Location,
        timeZone: TimeZone,
    ): List<PrayerTime> = executeApiSafely<PrayerTimesDto> {
        prayerTimeApiService.getPrayerTimes(
            date = date.toDateString(timeZone = timeZone),
            latitude = location.latitude,
            longitude = location.longitude
        )
    }.toDomain()

    override suspend fun getPrayerTimeInHijriDate(
        date: String,
        location: Location,
        timeZone: TimeZone
    ): List<PrayerTime> {
        // Fake implementation returning mock prayer times
        val currentTime = Clock.System.now()

        return listOf(
            PrayerTime(
                name = PrayerName.FAJR,
                time = currentTime.plus(3600.seconds),
                hijriDate = date
            ),
            PrayerTime(
                name = PrayerName.DHUHR,
                time = currentTime.plus(28800.seconds),
                hijriDate = date
            ),
            PrayerTime(
                name = PrayerName.ASR,
                time = currentTime.plus(43200.seconds),
                hijriDate = date
            ),
            PrayerTime(
                name = PrayerName.MAGHRIB,
                time = currentTime.plus(57600.seconds),
                hijriDate = date
            ),
            PrayerTime(
                name = PrayerName.ISHA,
                time = currentTime.plus(64800.seconds),
                hijriDate = date
            )
        )
    }

    private fun Instant.toDateString(timeZone: TimeZone): String =
        this.toLocalDateTime(timeZone = timeZone).date.format(
            format = LocalDate.Formats.ISO
        )
}
