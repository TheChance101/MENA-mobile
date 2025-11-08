package net.thechance.mena.faith.presentation.feature.qiblah.compass

sealed interface CompassEffect {
    data object NavigateBack : CompassEffect
    data object NavigateToAddressesScreen : CompassEffect
}