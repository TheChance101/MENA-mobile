package net.thechance.mena.faith.presentation.feature.qiblah.compass

import net.thechance.mena.faith.presentation.base.BaseViewModel

class CompassViewModel() : BaseViewModel<CompassScreenState, CompassEffect>(CompassScreenState()),
    CompassInteractionListener {

    override fun onBackClick() = sendEffect(CompassEffect.NavigateBack)
}