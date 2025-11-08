package net.thechance.mena.faith.presentation.utils.extentions

import kotlin.math.round

internal fun Double.roundTo2Decimals(): Double {
    return round(this * 100) / 100
}