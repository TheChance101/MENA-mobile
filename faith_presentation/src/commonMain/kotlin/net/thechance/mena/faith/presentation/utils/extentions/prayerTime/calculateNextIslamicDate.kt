package net.thechance.mena.faith.presentation.utils.extentions.prayerTime

import net.thechance.mena.faith.presentation.utils.IslamicDate

fun calculateNextIslamicDate(currentDate: IslamicDate): IslamicDate {
    var day = currentDate.day + 1
    var month = currentDate.month
    var year = currentDate.year

    val daysInMonth = getIslamicMonthDays(month = month, year = year)
    if (day > daysInMonth) {
        day = 1
        month++
        if (month > 12) {
            month = 1
            year++
        }
    }

    return IslamicDate(day = day, month = month, year = year)
}