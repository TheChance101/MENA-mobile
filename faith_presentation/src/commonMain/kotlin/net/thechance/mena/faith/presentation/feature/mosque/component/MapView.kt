package net.thechance.mena.faith.presentation.feature.mosque.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.faith.presentation.feature.mosque.MosqueUiState

@Composable
expect fun MapView(
    modifier: Modifier,
    centerLatitude: Double,
    centerLongitude: Double,
    zoomLevel: Double,
    markers: List<MosqueUiState>,
    canMove: Boolean,
    onMarkerClick: (MosqueUiState) -> Unit,
    onCameraMove: (Double, Double) -> Unit,
    onMapIdle: (Double, Double) -> Unit
)