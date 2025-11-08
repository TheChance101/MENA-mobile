package net.thechance.mena.dukan.presentation.util

import kotlin.math.round
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun Double.rounded(): Double = (round(this * 100) / 100)

@OptIn(ExperimentalTime::class)
fun ByteArray.toFileName(): String {
    return "${Clock.System.now().toEpochMilliseconds()}+product_image"
}

fun String.formatPrice(): String {
    return this.let { str ->
        if ('.' in str) {
            val (intPart, decimalPart) = str.split('.')
            val limitedDecimal = decimalPart.take(2)
            "$intPart,$limitedDecimal"
        } else {
            "$str,00"
        }
    }
}