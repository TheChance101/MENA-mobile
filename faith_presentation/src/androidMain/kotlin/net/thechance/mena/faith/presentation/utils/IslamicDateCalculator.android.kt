package net.thechance.mena.faith.presentation.utils

import android.icu.util.Calendar
import android.icu.util.GregorianCalendar
import android.icu.util.IslamicCalendar

actual class IslamicDateCalculatorImpl : IslamicDateCalculator {
    actual override fun gregorianToHijri(day: Int, month: Int, year: Int): IslamicDate {
        val gregorianCalendar = GregorianCalendar().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
        }

        val hijriCalendar = IslamicCalendar().apply {
            time = gregorianCalendar.time
        }

        return IslamicDate(
            day = hijriCalendar.get(Calendar.DAY_OF_MONTH),
            month = hijriCalendar.get(Calendar.MONTH) + 1,
            year = hijriCalendar.get(Calendar.YEAR)
        )
    }
}