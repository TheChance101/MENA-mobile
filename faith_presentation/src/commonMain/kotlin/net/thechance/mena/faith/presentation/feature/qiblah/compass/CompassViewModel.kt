package net.thechance.mena.faith.presentation.feature.qiblah.compass

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.faith.domain.usecase.QiblahBearingCalculatorUseCase
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.util.AzimuthProvider

class CompassViewModel(
    private val bearingCalculatorUseCase: QiblahBearingCalculatorUseCase,
    private val azimuthProvider: AzimuthProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<CompassScreenState, CompassEffect>(CompassScreenState()),
    CompassInteractionListener {
    private var currentContinuousAzimuth: Float = 0f

    init {
        getQiblahAngle()
    }

    override fun onBackClick() = sendEffect(CompassEffect.NavigateBack)

    private fun getQiblahAngle() {
        tryToExecute(
            dispatcher = dispatcher,
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
            dispatcher = dispatcher,
            block = azimuthProvider::startListening,
            onEmitNewValue = ::onAzimuthValueChange,
        )
    }

    private fun onAzimuthValueChange(rawAzimuth: Float) {
        val continuousAzimuth = calculateContinuousAzimuth(rawAzimuth)
        val relativeAngle =
            calculateRelativeAngleToQiblah(rawAzimuth, uiState.value.qiblahAngleValue)
        updateState {
            it.copy(
                continuousAzimuth = continuousAzimuth,
                angleToQiblah = relativeAngle
            )
        }
    }

    private fun calculateContinuousAzimuth(rawAzimuth: Float): Float {
        val oldAngleOnCircle = currentContinuousAzimuth % 360
        val diff = getShortestAngleDifference(from = oldAngleOnCircle, to = rawAzimuth)
        currentContinuousAzimuth += diff
        return currentContinuousAzimuth
    }

    private fun calculateRelativeAngleToQiblah(rawAzimuth: Float, qiblahAngle: Float): Float {
        return getShortestAngleDifference(from = rawAzimuth, to = qiblahAngle)
    }

    private fun getShortestAngleDifference(from: Float, to: Float): Float {
        val diff = (to - from) % 360
        return when {
            diff > 180f -> diff - 360f
            diff < -180f -> diff + 360f
            else -> diff
        }
    }
}