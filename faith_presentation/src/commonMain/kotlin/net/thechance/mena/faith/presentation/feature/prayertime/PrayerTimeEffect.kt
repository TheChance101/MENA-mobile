package net.thechance.mena.faith.presentation.feature.prayertime

sealed class PrayerTimeEffect {
    data object NavigateBack : PrayerTimeEffect()
    data object NavigateToAddressesScreen : PrayerTimeEffect()
}