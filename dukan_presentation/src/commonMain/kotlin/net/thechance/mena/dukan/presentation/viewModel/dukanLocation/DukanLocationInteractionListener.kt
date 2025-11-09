package net.thechance.mena.dukan.presentation.viewModel.dukanLocation

import org.maplibre.compose.camera.CameraPosition

interface DukanLocationInteractionListener {
    fun onBackClicked()

    fun onCameraMoved(camera: CameraPosition)
}