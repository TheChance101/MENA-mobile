package net.thechance.mena.wallet.presentation.utils

import kotlin.math.abs

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
    val parts = number.toString().split(".")
    val integerPart = parts[0].reversed().chunked(3).joinToString(",").reversed()
    val decimalPart = if (parts.size > 1 && parts[1].take(2).toInt() > 1) parts[1].take(2) else null

    return if (decimalPart != null && decimalPart.isNotEmpty()) {
        "$integerPart.${decimalPart.trimEnd('0').ifEmpty { "0" }}"
    } else {
        integerPart
    }
}