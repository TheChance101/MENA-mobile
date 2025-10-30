package net.thechance.mena.faith.presentation.feature.mosque.map

import androidx.compose.runtime.Composable
import io.github.dellisd.spatialk.geojson.Position
import net.thechance.mena.faith.presentation.feature.mosque.component.SimpleMapScreen
import org.maplibre.compose.camera.CameraPosition

@Composable
fun MosqueMap() {
    SimpleMapScreen(
        initialCameraPosition = CameraPosition(
            target = Position(
                longitude = 14.7749,
                latitude = -5.4194
            ),
            zoom = 14.0
        )
    )
}