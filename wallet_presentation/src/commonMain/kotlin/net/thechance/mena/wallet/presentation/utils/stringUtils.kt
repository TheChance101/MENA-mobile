package net.thechance.mena.wallet.presentation.utils

import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.round
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
    val isNegative = number < 0
    val absNumber = number.absoluteValue

    val longPart = absNumber.toLong()
    val decimalPart = round((absNumber - longPart) * 100).toInt()

    val integerStr = longPart.toString()
    val integerPart = integerStr.reversed().chunked(3).joinToString(",").reversed()

    val signedIntegerPart = if (isNegative) "-$integerPart" else integerPart

    return if (decimalPart > 0) {
        val decimalStr = decimalPart.toString().padStart(2, '0').trimEnd('0')
        "$signedIntegerPart.$decimalStr"
    } else {
        signedIntegerPart
    }
}