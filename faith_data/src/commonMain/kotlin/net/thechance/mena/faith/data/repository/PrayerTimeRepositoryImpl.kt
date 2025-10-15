package net.thechance.mena.faith.data.repository

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import net.thechance.mena.faith.data.remote.dto.prayertime.PrayerTimesDto
import net.thechance.mena.faith.data.remote.mapper.prayertime.toDomain
import net.thechance.mena.faith.data.remote.service.PrayerTimeApiService
import net.thechance.mena.faith.data.utils.executeApiSafely
import net.thechance.mena.faith.domain.entity.Location
import net.thechance.mena.faith.domain.entity.PrayerTime
import net.thechance.mena.faith.domain.repository.PrayerTimeRepository
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
            date = date.toDateString(),
            latitude = location.latitude,
            longitude = location.longitude
        )
    }.toDomain(timeZone)

    private fun Instant.toDateString(): String {
        val dateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
        val month = dateTime.month.number.toString().padStart(2, '0')
        val day = dateTime.day.toString().padStart(2, '0')
        return "$day-$month-${dateTime.year}"
    }
}
