package net.thechance.mena.faith.presentation.feature.qiblah.compass

import net.thechance.mena.faith.domain.usecase.QiblahBearingCalculatorUseCase
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.util.AzimuthProvider

class CompassViewModel(
    private val bearingCalculatorUseCase: QiblahBearingCalculatorUseCase,
    private val azimuthProvider: AzimuthProvider
) : BaseViewModel<CompassScreenState, CompassEffect>(CompassScreenState()),
    CompassInteractionListener {
    private var currentContinuousAzimuth: Float = 0f

    init {
        getQiblahAngle()
    }

    private fun getQiblahAngle() {
        tryToExecute(
            execute = bearingCalculatorUseCase::calculateQiblahAngle,
            onSuccess = ::onGetQiblahSuccess
        )
    }

    private fun onGetQiblahSuccess(angle: Double) {
        updateState { it.copy(qiblahAngleValue = angle.toFloat()) }
        startListeningOnAzimuth()
    }

    private fun startListeningOnAzimuth() {
        tryToCollect(
            block = azimuthProvider::startListening,
            onEmitNewValue = ::onAzimuthValueChange,
        )
    }

    private fun onAzimuthValueChange(rawAzimuth: Float) {
        val oldAngleOnCircle = currentContinuousAzimuth % 360
        var diff = rawAzimuth - oldAngleOnCircle
        if (diff > 180f) {
            diff -= 360f
        } else if (diff < -180f) {
            diff += 360f
        }
        currentContinuousAzimuth += diff
        var relativeAngle = uiState.value.qiblahAngleValue - rawAzimuth
        while (relativeAngle <= -180) relativeAngle += 360
        while (relativeAngle > 180) relativeAngle -= 360
        updateState {
            it.copy(
                continuousAzimuth = currentContinuousAzimuth,
                angleToQiblah = relativeAngle
            )
        }
    }

    override fun onBackClick() = sendEffect(CompassEffect.NavigateBack)
}