package net.thechance.mena.dukan.presentation.viewModel.dukanLocation

import androidx.compose.ui.unit.DpOffset
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanUiState
import org.maplibre.compose.camera.CameraPosition

class DukanLocationViewModel(
    savedStateHandle: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<DukanLocationUiState, DukanLocationEffect>(
    initialState = DukanLocationUiState(),
    defaultDispatcher = defaultDispatcher
), DukanLocationInteractionListener {
    private val args = savedStateHandle.toRoute<DukanRoute.DukanDetails>()

    override fun onBackClicked() {
        emitEffect(DukanLocationEffect.NavigateBack)
    }

    override fun onMapClicked(
        coordinates: CreateDukanUiState.CoordinatesUiState,
        pointerLocation: DpOffset
    ) {
    }

    override fun onCameraMoved(camera: CameraPosition) {
        updateState { copy(cameraPosition = camera) }
    }
}