package net.thechance.mena.faith.presentation.utils.extentions

import kotlin.math.round

internal fun Double.roundTo2Decimals() = round(this * 100) / 100