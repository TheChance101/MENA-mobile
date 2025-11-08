package net.thechance.mena.faith.presentation.feature.prayertime

sealed class PrayerTimeEffect {
    data object NavigateBack : PrayerTimeEffect()
    data object NavigatePrevDate : PrayerTimeEffect()
    data object NavigateNextDate : PrayerTimeEffect()
    data object NavigateToAddressesScreen : PrayerTimeEffect()
    data object NavigateCalenderDialog : PrayerTimeEffect()
}