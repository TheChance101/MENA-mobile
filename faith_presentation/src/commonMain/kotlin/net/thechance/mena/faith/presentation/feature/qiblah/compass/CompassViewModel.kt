package net.thechance.mena.faith.presentation.feature.qiblah.compass

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.faith.domain.usecase.QiblahBearingCalculatorUseCase
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.utils.AzimuthProvider
import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.service.LocationService

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
        if (uiState.value.city.isNotEmpty()) {
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
            dispatcher = dispatcher,
            execute = { locationService.getActiveAddress() },
            onSuccess = ::handleAddressResult
        )
    }

    private fun handleAddressResult(address: Address?) {
        when {
            address == null -> navigateToMyLocation()
            address.hasEmptyAddressLine() -> navigateToEnableLocation(address)
            else -> processValidAddress(address)
        }
    }

    private fun navigateToMyLocation() {
        sendEffect(CompassEffect.NavigateToMyLocation)
    }

    private fun navigateToEnableLocation(address: Address) {
        updateState { it.copy(city = address.addressLine) }
        sendEffect(CompassEffect.NavigateToEnableLocation)
    }

    private fun processValidAddress(address: Address) {
        updateState { it.copy(city = address.addressLine) }
        calculateQiblahDirection(address)
    }

    private fun Address.hasEmptyAddressLine(): Boolean = addressLine.isEmpty()


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