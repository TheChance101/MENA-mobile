package net.thechance.mena.dukan.presentation.viewModel.dukanLocation

import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.test.runTest
import org.maplibre.compose.camera.CameraPosition
import kotlin.test.Test
import kotlin.test.assertEquals

class DukanLocationUiStateTest {

    @Test
    fun `default state SHOULD have default camera position`() = runTest {
        val state = DukanLocationUiState()

        assertEquals(29.0, state.cameraPosition.target.longitude)
        assertEquals(28.0, state.cameraPosition.target.latitude)
        assertEquals(1.0, state.cameraPosition.zoom)
    }

    @Test
    fun `copy SHOULD update camera position`() = runTest {
        val initial = DukanLocationUiState()
        val newCamera = CameraPosition(target = Position(40.0, 41.0), zoom = 12.0)

        val updated = initial.copy(cameraPosition = newCamera)

        assertEquals(40.0, updated.cameraPosition.target.longitude)
        assertEquals(41.0, updated.cameraPosition.target.latitude)
        assertEquals(12.0, updated.cameraPosition.zoom)
    }
}
