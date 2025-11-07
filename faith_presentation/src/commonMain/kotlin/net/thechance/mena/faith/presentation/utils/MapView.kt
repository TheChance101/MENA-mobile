package net.thechance.mena.faith.presentation.utils

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
    onMarkerClick: (MosqueUiState) -> Unit,
    onMapClick: (Double, Double) -> Unit,
    onCameraMove: (Double, Double) -> Unit
)