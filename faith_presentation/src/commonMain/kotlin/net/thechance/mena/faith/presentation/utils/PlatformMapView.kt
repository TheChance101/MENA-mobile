package net.thechance.mena.faith.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class MapMarker @OptIn(ExperimentalUuidApi::class) constructor(
    val id: Uuid,
    val latitude: Double,
    val longitude: Double,
    val title: String? = null,
    val snippet: String? = null,
)



@Composable
expect fun MapView(
    modifier: Modifier = Modifier,
    centerLatitude: Double,
    centerLongitude: Double,
    zoomLevel: Double = 14.0,
    markers: List<MapMarker> = emptyList(),
    onMarkerClick: (MapMarker) -> Unit = {},
    onMapClick: (latitude: Double, longitude: Double) -> Unit = { _, _ -> },
    onCameraMove: (latitude: Double, longitude: Double) -> Unit = { _, _ -> },
)