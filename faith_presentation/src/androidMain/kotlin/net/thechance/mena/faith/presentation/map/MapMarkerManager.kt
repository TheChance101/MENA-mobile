package net.thechance.mena.faith.presentation.map

import android.content.Context
import androidx.core.content.ContextCompat
import net.thechance.mena.faith.presentation.R
import net.thechance.mena.faith.presentation.utils.MapMarker
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapMarkerManager(
    private val context: Context,
    private val onMarkerClick: (MapMarker) -> Unit
) {
    private val clusterer = MarkerClusterer()

    fun updateMarkers(
        mapView: MapView,
        markers: List<MapMarker>,
        zoomLevel: Double
    ) {
        clearAllMarkers(mapView)

        if (shouldShowIndividualMarkers(zoomLevel)) {
            addIndividualMarkers(mapView, markers)
        } else {
            addClusteredMarkers(mapView, markers, zoomLevel)
        }

        mapView.invalidate()
    }

    private fun shouldShowIndividualMarkers(zoomLevel: Double): Boolean {
        return zoomLevel >= MapConstants.FULL_DETAIL_ZOOM
    }

    private fun clearAllMarkers(mapView: MapView) {
        mapView.overlays.removeAll { it is Marker }
    }

    private fun addIndividualMarkers(mapView: MapView, markers: List<MapMarker>) {
        markers.forEach { markerData ->
            val marker = createIndividualMarker(mapView, markerData)
            mapView.overlays.add(marker)
        }
    }

    private fun addClusteredMarkers(
        mapView: MapView,
        markers: List<MapMarker>,
        zoomLevel: Double
    ) {
        val clusterDistance = MapUtils.getClusterDistance(zoomLevel)
        val clusters = clusterer.clusterMarkers(markers, clusterDistance)

        clusters.forEach { cluster ->
            val marker = if (cluster.isSingleMarker) {
                createIndividualMarker(mapView, cluster.markers.first())
            } else {
                createClusterMarker(mapView, cluster)
            }
            mapView.overlays.add(marker)
        }
    }

    private fun createIndividualMarker(mapView: MapView, markerData: MapMarker): Marker {
        return Marker(mapView).apply {
            position = GeoPoint(markerData.latitude, markerData.longitude)
            title = markerData.title
            snippet = markerData.snippet
            icon = ContextCompat.getDrawable(context, R.drawable.marker)
            setOnMarkerClickListener { _, _ ->
                onMarkerClick(markerData)
                true
            }
        }
    }

    private fun createClusterMarker(mapView: MapView, cluster: MarkerCluster): Marker {
        return Marker(mapView).apply {
            position = GeoPoint(cluster.centerLatitude, cluster.centerLongitude)
            title = cluster.count.toString()
            icon = ClusterIconFactory.createClusterIcon(context, cluster.count)
            setOnMarkerClickListener { _, _ -> true }
        }
    }

}