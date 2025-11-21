package net.thechance.mena.faith.presentation.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class IslamicDateCalculatorImpl: IslamicDateCalculator {
    override fun gregorianToHijri(day: Int, month: Int, year: Int): IslamicDate
}

interface IslamicDateCalculator {
    fun gregorianToHijri(day: Int, month: Int, year: Int): IslamicDate
}

data class IslamicDate(
    val day: Int,
    val month: Int,
    val year: Int
) {
    companion object {
        @OptIn(ExperimentalTime::class)
        fun now(calculator: IslamicDateCalculator): IslamicDate {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            return calculator.gregorianToHijri(now.day, now.month.number, now.year)
        }
    }
}