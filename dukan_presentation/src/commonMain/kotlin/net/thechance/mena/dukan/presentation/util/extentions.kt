package net.thechance.mena.dukan.presentation.util

import kotlin.math.round
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

 fun Double.rounded(): Double = (round(this * 100) / 100)
@OptIn(ExperimentalTime::class)
 fun ByteArray.toFileName(): String {
    return "${Clock.System.now().toEpochMilliseconds()}+product_image"
}
