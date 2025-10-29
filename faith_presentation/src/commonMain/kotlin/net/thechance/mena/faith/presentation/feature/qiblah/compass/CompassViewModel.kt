package net.thechance.mena.faith.presentation.feature.qiblah.compass

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.faith.domain.usecase.QiblahBearingCalculatorUseCase
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.utils.AzimuthProvider
import net.thechance.mena.identity.domain.entity.Address
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
        loadCompassData()
    }

    override fun onBackClick() = sendEffect(CompassEffect.NavigateBack)
    override fun onChangeLocation() {
        if (isValidAddress(uiState.value.city)) {
            sendEffect(CompassEffect.NavigateToEnableLocation)
            return
        }
        sendEffect(CompassEffect.NavigateToMyLocation)
    }
    fun refreshAddress() {
        loadCompassData()
    }

    private fun loadCompassData() {
        tryToExecute(
            execute = { locationService.getActiveAddress() },
            onSuccess = ::handleAddressResult
        )
    }

    private fun handleAddressResult(address: Address?) {
        if (!isValidAddress(uiState.value.city)) {
            sendEffect(CompassEffect.NavigateToEnableLocation)
            return
        }
        updateState { it.copy(city = address?.addressLine ?: "") }
        calculateQiblahDirection(address!!)
    }

    private fun isValidAddress(address: String): Boolean = address.isEmpty()

    private fun calculateQiblahDirection(address: Address) {
        tryToExecute(
            dispatcher = dispatcher,
            execute = { bearingCalculatorUseCase.calculateQiblahAngle(address) },
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