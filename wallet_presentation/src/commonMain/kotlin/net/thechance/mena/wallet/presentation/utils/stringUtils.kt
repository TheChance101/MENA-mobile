package net.thechance.mena.wallet.presentation.utils

import kotlin.math.abs
import kotlin.math.absoluteValue

fun formatBalance(balance: Double): String {
    val wholePart = balance.toLong()
    val decimalPart = ((abs(balance) % 1) * 100).toLong()

    val wholeString = wholePart.toString()
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()

    val decimalString = decimalPart.toString().padStart(2, '0')

    return "$wholeString.$decimalString"
}
fun formatAmount(number: Double): String {
    val numberStr = if (number.absoluteValue >= 1e10) {
        number.toLong().toString() + ".00"
    } else {
        number.toString()
    }

    val parts = numberStr.split(".")
    val integerPart = parts[0].reversed().chunked(3).joinToString(",").reversed()
    val decimalPart = if (parts.size > 1) {
        parts[1].take(2).trimEnd('0')
    } else {
        null
    }

    return if (!decimalPart.isNullOrEmpty()) {
        "$integerPart.$decimalPart"
    } else {
        integerPart
    }
}