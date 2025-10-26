package net.thechance.mena.faith.presentation.feature.qiblah.compass

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.faith.domain.usecase.QiblahBearingCalculatorUseCase
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.utils.AzimuthProvider
import net.thechance.mena.identity.domain.service.LocationService
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class CompassViewModel(
    private val bearingCalculatorUseCase: QiblahBearingCalculatorUseCase,
    private val locationService: LocationService,
    private val azimuthProvider: AzimuthProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<CompassUiState, CompassEffect>(CompassUiState()),
    CompassInteractionListener {

    init {
        getCurrentAddress()
        getQiblahAngle()
    }

    override fun onBackClick() = sendEffect(CompassEffect.NavigateBack)

    private fun getCurrentAddress() {
        tryToExecute(
            execute = {
                val address = locationService.getActiveAddress()
                address?.toAddressUi() ?: AddressUi()
            },
            onSuccess = ::onGetAddressSuccess
        )
    }

    private fun onGetAddressSuccess(address: AddressUi) {
        updateState { state -> state.copy(currentLocationUi = address) }
    }

    private fun getQiblahAngle() {
        tryToExecute(
            dispatcher = dispatcher,
            execute = { bearingCalculatorUseCase.calculateQiblahAngle(uiState.value.currentLocationUi.toAddress()) },
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
        val continuousAzimuth = bearingCalculatorUseCase.calculateContinuousAzimuth(rawAzimuth)
        val relativeAngle =
            calculateRelativeAngleToQiblah(rawAzimuth, uiState.value.qiblahAngleValue)
        updateState {
            it.copy(
                continuousAzimuth = continuousAzimuth,
                angleToQiblah = relativeAngle
            )
        }
    }

    private fun calculateRelativeAngleToQiblah(rawAzimuth: Float, qiblahAngle: Float): Float {
        return bearingCalculatorUseCase.getShortestAngleDifference(
            from = rawAzimuth,
            to = qiblahAngle
        )
    }
}