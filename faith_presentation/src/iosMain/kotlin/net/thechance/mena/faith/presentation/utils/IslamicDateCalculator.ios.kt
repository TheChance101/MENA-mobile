package net.thechance.mena.faith.presentation.utils

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarIdentifierGregorian
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDateComponents
import platform.Foundation.NSIslamicCivilCalendar

actual class IslamicDateCalculatorImpl :IslamicDateCalculator {
    actual override fun gregorianToHijri(day: Int, month: Int, year: Int): IslamicDate {
        var gregorianComponents = NSDateComponents().apply {
            this.day = day.toLong()
            this.month = month.toLong()
            this.year = year.toLong()

        }

        val gregorianCalender = NSCalendar.calendarWithIdentifier(NSCalendarIdentifierGregorian)

        val date = gregorianCalender?.dateFromComponents(gregorianComponents) ?: return IslamicDate(1,1,1447)

        val hijriCalendar = NSCalendar.calendarWithIdentifier(NSIslamicCivilCalendar)
        val islamicDate = hijriCalendar?.components(
            NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay,
            fromDate = date,
        ) ?: return IslamicDate(1,1,1447)

        return IslamicDate(
            year = islamicDate.year.toInt(),
            month = islamicDate.month.toInt(),
            day = islamicDate.day.toInt(),
        )
    }
}