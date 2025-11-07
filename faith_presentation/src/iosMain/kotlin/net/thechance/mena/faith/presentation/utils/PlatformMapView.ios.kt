package net.thechance.mena.faith.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun OsmMapView(
    modifier: Modifier,
    centerLatitude: Double,
    centerLongitude: Double,
    zoomLevel: Double,
    markers: List<MapMarker>,
    onMarkerClick: (MapMarker) -> Unit,
    onMapClick: (Double, Double) -> Unit,
    onCameraMove: (Double, Double) -> Unit
) {

}