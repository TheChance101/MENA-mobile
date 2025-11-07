package net.thechance.mena.faith.presentation.utils

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import net.thechance.mena.faith.presentation.feature.mosque.MosqueUiState
import net.thechance.mena.faith.presentation.map.MapConfigurator
import net.thechance.mena.faith.presentation.map.MapConstants
import net.thechance.mena.faith.presentation.map.MapMarkerManager
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import kotlin.math.abs

@Composable
actual fun MapView(
    modifier: Modifier,
    centerLatitude: Double,
    centerLongitude: Double,
    zoomLevel: Double,
    markers: List<MosqueUiState>,
    onMarkerClick: (MosqueUiState) -> Unit,
    onMapClick: (Double, Double) -> Unit,
    onCameraMove: (Double, Double) -> Unit
) {
    val context = LocalContext.current
    var currentZoom by remember { mutableDoubleStateOf(zoomLevel) }
    val mapViewRef = remember { mutableStateOf<MapView?>(null) }
    val markerManager = remember(context, onMarkerClick) {
        MapMarkerManager(context, onMarkerClick)
    }
    ConfigureOsmDroid(context)

    ObserveZoomChanges(
        currentZoom = currentZoom,
        markers = markers,
        mapViewRef = mapViewRef,
        markerManager = markerManager
    )

    AndroidView(
        modifier = modifier
            .clipToBounds()
            .fillMaxSize(),
        factory = { ctx ->
            createMapView(
                context = ctx,
                zoomLevel = zoomLevel,
                centerLatitude = centerLatitude,
                centerLongitude = centerLongitude,
                onZoomChange = { newZoom ->
                    if (abs(newZoom - currentZoom) >= MapConstants.ZOOM_CHANGE_THRESHOLD) {
                        currentZoom = newZoom
                    }
                },
                onMapClick = onMapClick,
                onCameraMove = onCameraMove
            ).also { mapView ->
                mapViewRef.value = mapView
            }
        },
        update = {
        }
    )
    LaunchedEffect(centerLatitude, centerLongitude) {
        val mapView = mapViewRef.value ?: return@LaunchedEffect
        if (centerLatitude != 0.0 && centerLongitude != 0.0) {
            delay(300)
            val newCenter = GeoPoint(centerLatitude, centerLongitude)
            mapView.controller.animateTo(newCenter)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mapViewRef.value = null
        }
    }
}

@Composable
private fun ConfigureOsmDroid(context: Context) {
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )
    }
}

@Composable
private fun ObserveZoomChanges(
    currentZoom: Double,
    markers: List<MosqueUiState>,
    mapViewRef: MutableState<MapView?>,
    markerManager: MapMarkerManager
) {
    LaunchedEffect(currentZoom, markers) {
        snapshotFlow { currentZoom }.collectLatest { zoom ->
            delay(MapConstants.CLUSTER_UPDATE_DELAY)
            mapViewRef.value?.let { mapView ->
                markerManager.updateMarkers(mapView, markers, zoom)
            }
        }
    }
}

private fun createMapView(
    context: Context,
    zoomLevel: Double,
    centerLatitude: Double,
    centerLongitude: Double,
    onZoomChange: (Double) -> Unit,
    onMapClick: (Double, Double) -> Unit,
    onCameraMove: (Double, Double) -> Unit
): MapView {
    return MapView(context).apply {
        MapConfigurator.configure(
            mapView = this,
            initialZoom = zoomLevel,
            centerLatitude = centerLatitude,
            centerLongitude = centerLongitude
        )
        this.zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)
        this.setMultiTouchControls(true)
        setupZoomListener(onZoomChange, onCameraMove)
    }
}

private fun MapView.setupZoomListener(
    onZoomChange: (Double) -> Unit,
    onCameraMove: (Double, Double) -> Unit
) {
    addMapListener(object : org.osmdroid.events.MapListener {
        override fun onScroll(event: org.osmdroid.events.ScrollEvent?): Boolean {
            event?.source?.mapCenter?.let {
                onCameraMove(it.latitude, it.longitude)
            }
            return true
        }

        override fun onZoom(event: org.osmdroid.events.ZoomEvent?): Boolean {
            event?.let {
                onZoomChange(it.zoomLevel)
                it.source.mapCenter?.let { center ->
                    onCameraMove(center.latitude, center.longitude)
                }
            }
            return true
        }
    })
}


