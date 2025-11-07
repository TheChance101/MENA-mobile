package net.thechance.mena.faith.presentation.feature.prayertime

interface PrayerTimeInteractionListener {
    fun onBackClick()
    fun onPrevDateClick()
    fun onNextDateClick()
    fun onDateDropdownClick()
    fun onLocationClick()
}