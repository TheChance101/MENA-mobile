package net.thechance.mena.faith.domain.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.thechance.mena.faith.domain.entity.PrayerTime
import net.thechance.mena.faith.domain.repository.PrayerTimeRepository
import net.thechance.mena.identity.domain.entity.Address
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class PrayerTimeService(val prayerTimeRepository: PrayerTimeRepository) {

    @OptIn(ExperimentalTime::class)
    fun getNextPrayer(address: Address): Flow<PrayerTime?> = flow {
        val now = Clock.System.now()
        val todayPrayers = prayerTimeRepository.getPrayerTimes(date = now, address = address)

        if (todayPrayers.isEmpty()) emit(null)

        val nextPrayer = todayPrayers.firstOrNull { it.time > now }

        if (nextPrayer != null) emit(nextPrayer)
        else {
            val tomorrow = now.plusDays(1)
            val tomorrowPrayers =
                prayerTimeRepository.getPrayerTimes(date = tomorrow, address = address)
            emit(tomorrowPrayers.firstOrNull())
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun Instant.plusDays(days: Int): Instant {
        val x = this.toEpochMilliseconds() + days * 24 * 60 * 60 * 1000
        return Instant.fromEpochMilliseconds(x)
    }
}