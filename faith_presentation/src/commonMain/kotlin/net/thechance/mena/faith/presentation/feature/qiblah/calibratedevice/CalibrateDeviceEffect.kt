package net.thechance.mena.faith.presentation.feature.qiblah.calibratedevice

sealed interface CalibrateDeviceEffect {
    data object NavigateBack : CalibrateDeviceEffect
    data object NavigateToQiblah : CalibrateDeviceEffect
}
