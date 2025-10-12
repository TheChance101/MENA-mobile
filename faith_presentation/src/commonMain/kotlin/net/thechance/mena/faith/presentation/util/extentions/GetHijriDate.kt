package net.thechance.mena.faith.presentation.util.extentions

import net.thechance.mena.faith.domain.entity.PrayerTime

fun getHijriDate(prayerTimes: List<PrayerTime>): String =
    prayerTimes.firstOrNull()?.hijriDate ?: ""