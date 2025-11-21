package net.thechance.mena.faith.presentation.utils.extentions.prayerTime

fun getIslamicMonthDays(month: Int, year: Int): Int {
        return when (month) {
            1, 3, 5, 7, 9, 11 -> 30
            2, 4, 6, 8, 10 -> 29
            12 -> if (isIslamicLeapYear(year)) 30 else 29
            else -> 30
        }
    }

    private fun isIslamicLeapYear(year: Int): Boolean = (year * 11 + 14) % 30 < 11