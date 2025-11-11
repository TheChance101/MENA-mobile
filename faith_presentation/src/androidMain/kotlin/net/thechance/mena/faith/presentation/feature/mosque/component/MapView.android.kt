package net.thechance.mena.faith.presentation.feature.mosque.component

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.thechance.mena.faith.presentation.feature.mosque.MosqueUiState
import net.thechance.mena.faith.presentation.map.MapConfigurator
import net.thechance.mena.faith.presentation.map.MapConstants
import net.thechance.mena.faith.presentation.map.MapMarkerManager
import org.osmdroid.api.IGeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import kotlin.math.abs

@Composable
actual fun MapView(
    modifier: Modifier,
    centerLatitude: Double,
    centerLongitude: Double,
    zoomLevel: Double,
    markers: List<MosqueUiState>,
    canMove: Boolean,
    onMarkerClick: (MosqueUiState) -> Unit,
    onCameraMove: (Double, Double) -> Unit,
    onMapIdle: (Double, Double) -> Unit
) {
    val context = LocalContext.current
    var currentZoom by remember { mutableDoubleStateOf(zoomLevel) }
    val mapViewRef = remember { mutableStateOf<MapView?>(null) }
    val markerManager = remember(context, onMarkerClick) {
        MapMarkerManager(context, onMarkerClick)
    }
    ConfigureOsmDroid(context)



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
                onCameraMove = onCameraMove,
                onZoomChange = { newZoom ->
                    if (abs(newZoom - currentZoom) >= MapConstants.ZOOM_CHANGE_THRESHOLD) {
                        currentZoom = newZoom
                    }
                },
                onCameraIdle = { lat, lon ->
                    onMapIdle(lat, lon)
                },
            ).also { mapView ->
                mapViewRef.value = mapView
            }
        },
        update = {
        }
    )
    ObserveZoomChanges(
        currentZoom = currentZoom,
        markers = markers,
        mapViewRef = mapViewRef,
        markerManager = markerManager
    )
    LaunchedEffect(centerLatitude, centerLongitude, canMove) {
        if (canMove) {
            val mapView = mapViewRef.value ?: return@LaunchedEffect
            if (centerLatitude != 0.0 && centerLongitude != 0.0) {
                delay(300)
                val newCenter = GeoPoint(centerLatitude, centerLongitude)
                mapView.controller.animateTo(newCenter)

            }
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
    onCameraMove: (Double, Double) -> Unit,
    onCameraIdle: (Double, Double) -> Unit,
    onZoomChange: (Double) -> Unit,
): MapView {
    return MapView(context).apply {
        MapConfigurator.configure(
            mapView = this,
            initialZoom = zoomLevel,
            centerLatitude = centerLatitude,
            centerLongitude = centerLongitude
        )
        this.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        this.setMultiTouchControls(true)
        setupZoomListener(onCameraIdle, onCameraMove, onZoomChange)
    }
}

private fun MapView.setupZoomListener(
    onCameraIdle: (Double, Double) -> Unit,
    onCameraMove: (Double, Double) -> Unit,
    onZoomChange: (Double) -> Unit,
) {
    addMapListener(object : MapListener {
        var job: Job? = null
        var center: IGeoPoint? = null

        override fun onScroll(event: ScrollEvent?): Boolean {
            job?.cancel()
            job = CoroutineScope(Dispatchers.IO).launch {
                delay(2000)
                center = event?.source?.mapCenter
                center?.let {
                    onCameraIdle(it.latitude, it.longitude)
                }
            }
            event?.source?.mapCenter?.let {
                onCameraMove(it.latitude, it.longitude)
            }
            return true
        }

        override fun onZoom(event: ZoomEvent?): Boolean {
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