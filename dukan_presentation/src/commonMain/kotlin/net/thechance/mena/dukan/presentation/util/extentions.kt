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
        if (PRICE_DECIMAL_SEPARATOR in str) {
            val (intPart, decimalPart) = str.split(PRICE_DECIMAL_SEPARATOR)
            val limitedDecimal = decimalPart.take(2)
            "$intPart$NEW_PRICE_DECIMAL_SEPARATOR$limitedDecimal"
        } else {
            "$str${NEW_PRICE_DECIMAL_SEPARATOR}00"
        }
    }
}

fun filterPriceInput(price: String): String {
    return price.filter { it.isDigit() || it == PRICE_DECIMAL_SEPARATOR }
}

const val PRICE_DECIMAL_SEPARATOR = '.'
const val NEW_PRICE_DECIMAL_SEPARATOR = ','