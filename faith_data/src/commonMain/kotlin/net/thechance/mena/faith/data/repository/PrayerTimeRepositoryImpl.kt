package net.thechance.mena.faith.data.repository

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import net.thechance.mena.faith.data.mapper.prayertime.toDomain
import net.thechance.mena.faith.data.remote.model.prayertime.PrayerTimesDto
import net.thechance.mena.faith.data.remote.service.PrayerTimeApiService
import net.thechance.mena.faith.data.utils.executeApiSafely
import net.thechance.mena.faith.domain.entity.PrayerTime
import net.thechance.mena.faith.domain.repository.PrayerTimeRepository
import net.thechance.mena.identity.domain.entity.Address
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class PrayerTimeRepositoryImpl(
    private val prayerTimeApiService: PrayerTimeApiService
) : PrayerTimeRepository {

    override suspend fun getPrayerTimes(
        date: Instant,
        address: Address,
        timeZone: TimeZone,
    ): List<PrayerTime> = executeApiSafely<PrayerTimesDto> {
        prayerTimeApiService.getPrayerTimes(
            date = date.toDateString(timeZone = timeZone),
            latitude = address.latitude,
            longitude = address.longitude
        )
    }.toDomain()

    override suspend fun getPrayerTimeWithHijriDate(
        date: String,
        address: Address,
        timeZone: TimeZone,
        isHijri: Boolean
    ): List<PrayerTime> = executeApiSafely<PrayerTimesDto> {
        prayerTimeApiService.getPrayerTimes(
            date = date,
            latitude = address.latitude,
            longitude = address.longitude,
            isHijri = isHijri
        )
    }.toDomain()


    private fun Instant.toDateString(timeZone: TimeZone): String =
        this.toLocalDateTime(timeZone = timeZone).date.format(
            format = LocalDate.Formats.ISO
        )
}
