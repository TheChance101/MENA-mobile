package net.thechance.mena.faith.presentation.feature.main

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.asr
import mena.faith_presentation.generated.resources.dhuhr
import mena.faith_presentation.generated.resources.fajr
import mena.faith_presentation.generated.resources.isha
import mena.faith_presentation.generated.resources.maghrib
import mena.faith_presentation.generated.resources.sunrise
import mena.faith_presentation.generated.resources.surah_al_fatiha
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.model.LastAyahForTilawah
import net.thechance.mena.faith.presentation.utils.extentions.prayerTime.formatInstantToTimeString
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun getPrayerDisplayNameResource(prayerName: PrayerName): StringResource = when (prayerName) {
    PrayerName.FAJR -> Res.string.fajr
    PrayerName.DHUHR -> Res.string.dhuhr
    PrayerName.ASR -> Res.string.asr
    PrayerName.MAGHRIB -> Res.string.maghrib
    PrayerName.ISHA -> Res.string.isha
    PrayerName.SUNRISE -> Res.string.sunrise
}

@OptIn(ExperimentalTime::class)
fun isAM(instant: Instant): Boolean {
    val kotlinxInstant = Instant.fromEpochMilliseconds(instant.toEpochMilliseconds())
    val localDateTime = kotlinxInstant.toLocalDateTime(TimeZone.currentSystemDefault())
    return localDateTime.hour < 12
}

@OptIn(ExperimentalTime::class)
fun List<PrayerTime>.toUi(now: Instant): PrayerTimesUiState {
    val prayerTimesMap = associateBy { it.name }
    val prayerTimesUi = PRAYER_ORDER.mapNotNull { prayerTimesMap[it]?.toUi() }

    val currentPrayer = getCurrentPrayer(this, now)

    val nextPrayerIndex = when (currentPrayer) {
        PrayerName.FAJR -> PRAYER_ORDER.indexOf(PrayerName.DHUHR)
        PrayerName.DHUHR -> PRAYER_ORDER.indexOf(PrayerName.ASR)
        PrayerName.ASR -> PRAYER_ORDER.indexOf(PrayerName.MAGHRIB)
        PrayerName.MAGHRIB -> PRAYER_ORDER.indexOf(PrayerName.ISHA)
        PrayerName.ISHA -> PRAYER_ORDER.indexOf(PrayerName.FAJR)
        else -> 0
    }


    return PrayerTimesUiState(
        prayers = prayerTimesUi,
        nextPrayerIndex = nextPrayerIndex
    )
}

@OptIn(ExperimentalTime::class)
private fun PrayerTime.toUi(): PrayerUiModel = PrayerUiModel(
    name = this.name,
    displayName = getPrayerDisplayNameResource(prayerName = this.name),
    time = this.time.formatInstantToTimeString(withISPM = false),
    isAM = isAM(this.time)
)

@OptIn(ExperimentalTime::class)
private fun getCurrentPrayer(prayerTimes: List<PrayerTime>, now: Instant): PrayerName {
    val sortedPrayers = prayerTimes
        .filter { it.name != PrayerName.SUNRISE }
        .sortedBy { it.time }

    if (now < sortedPrayers.first().time || now >= sortedPrayers.last().time) {
        return sortedPrayers.last().name
    }

    return sortedPrayers.lastOrNull { now >= it.time }?.name ?: sortedPrayers.first().name
}

suspend fun LastAyahForTilawah.toTilawahUiState(): TilawahUiState {
    val surahName = Surah.SurahOrder.entries
        .find { it.order == this.surahId }
        ?.name ?: getString(Res.string.surah_al_fatiha)
    val ayahLabel =  number
    return TilawahUiState(
        surahName = surahName,
        ayahNumber = ayahLabel,
        surahId = this.surahId
    )
}

private val PRAYER_ORDER = listOf(
    PrayerName.FAJR,
    PrayerName.DHUHR,
    PrayerName.ASR,
    PrayerName.MAGHRIB,
    PrayerName.ISHA
)
