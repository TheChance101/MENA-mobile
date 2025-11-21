package net.thechance.mena.faith.presentation.utils.extentions.prayerTime

import net.thechance.mena.faith.presentation.utils.IslamicDate

fun calculatePreviousIslamicDate(currentDate: IslamicDate): IslamicDate {
    var day = currentDate.day - 1
    var month = currentDate.month
    var year = currentDate.year

    if (day < 1) {
        month--
        if (month < 1) {
            month = 12
            year--
        }
        day = getIslamicMonthDays(month = month, year = year)
    }

    return IslamicDate(day = day, month = month, year = year)
}