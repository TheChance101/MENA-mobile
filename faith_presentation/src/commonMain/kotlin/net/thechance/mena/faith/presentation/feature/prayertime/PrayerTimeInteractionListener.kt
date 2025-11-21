package net.thechance.mena.faith.presentation.feature.prayertime

interface PrayerTimeInteractionListener {
    fun onBackClick()
    fun onPrevDateClick()
    fun onNextDateClick()
    fun onDateDropdownClick()
    fun onLocationClick()
    fun onSelectedDateChange(day: Int, month: Int, year: Int)
    fun onDateSelected()
    fun onClearSelectedDate()
    fun onDatePickerDismiss()
}