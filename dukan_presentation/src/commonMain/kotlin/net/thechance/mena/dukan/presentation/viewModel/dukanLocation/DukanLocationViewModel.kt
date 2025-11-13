package net.thechance.mena.dukan.presentation.viewModel.dukanLocation

import androidx.lifecycle.SavedStateHandle
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import org.maplibre.compose.camera.CameraPosition

class DukanLocationViewModel(
    savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<DukanLocationUiState, DukanLocationEffect>(
    initialState = DukanLocationUiState(),
    defaultDispatcher = defaultDispatcher
), DukanLocationInteractionListener {
    private val latitude = savedStateHandle.get<Double>(LATITUDE) ?: 0.0
    private val longitude = savedStateHandle.get<Double>(LONGITUDE) ?: 0.0

    init {
        updateCameraPosition(latitude, longitude)
    }

    private fun updateCameraPosition(latitude: Double, longitude: Double) {
        updateState {
            copy(
                cameraPosition = CameraPosition(
                    target = Position(
                        longitude,
                        latitude
                    ),
                    zoom = DUKAN_LOCATION_ZOOM
                )
            )
        }
    }

    override fun onBackClicked() {
        emitEffect(DukanLocationEffect.NavigateBack)
    }

    companion object {
        const val DUKAN_LOCATION_ZOOM = 16.0
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
    }
}