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