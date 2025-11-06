package net.thechance.mena.faith.presentation.utils.extentions.prayerTime

import kotlin.math.round

fun Double.roundTo2Decimals(): Double {
    return round(this * 100) / 100
}
