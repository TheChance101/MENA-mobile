package net.thechance.mena.dukan.presentation.viewModel.dukanLocation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import org.maplibre.compose.camera.CameraPosition

class DukanLocationViewModel(
    savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<DukanLocationUiState, DukanLocationEffect>(
    initialState = DukanLocationUiState(),
    defaultDispatcher = defaultDispatcher
), DukanLocationInteractionListener {
    private val args = savedStateHandle.toRoute<DukanRoute.DukanLocation>()

    init {
        updateState {
            copy(
                cameraPosition = CameraPosition(
                    target = Position(
                        args.latitude,
                        args.longitude
                    ),
                    zoom = 20.0
                )
            )
        }
    }

    override fun onBackClicked() {
        emitEffect(DukanLocationEffect.NavigateBack)
    }


    override fun onCameraMoved(camera: CameraPosition) {
        updateState { copy(cameraPosition = camera) }
    }
}