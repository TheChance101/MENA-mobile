package net.thechance.mena.faith.presentation.feature.qiblah.calibratedevice

import net.thechance.mena.faith.presentation.base.BaseViewModel

class CalibrateDeviceViewModel() : BaseViewModel<Unit, CalibrateDeviceEffect>(Unit), CalibrateDeviceInteractionListener {

    override fun onBackClick() = sendEffect(CalibrateDeviceEffect.NavigateBack)
    override fun onContinueClick() = sendEffect(CalibrateDeviceEffect.NavigateToQiblah)

}
