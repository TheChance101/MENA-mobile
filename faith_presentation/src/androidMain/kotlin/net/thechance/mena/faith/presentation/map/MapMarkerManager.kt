package net.thechance.mena.faith.presentation.map

import android.content.Context
import androidx.core.content.ContextCompat
import net.thechance.mena.faith.presentation.R
import net.thechance.mena.faith.presentation.feature.mosque.MosqueUiState
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapMarkerManager(
    private val context: Context,
    private val onMarkerClick: (MosqueUiState) -> Unit
) {
    private val clusterer = MosqueClusterer()

    fun updateMarkers(
        mapView: MapView,
        mosques: List<MosqueUiState>,
        zoomLevel: Double
    ) {
        clearAllMarkers(mapView)

        val clusterDistance = MapUtils.getClusterDistance(zoomLevel)
        val clusters = clusterer.clusterMosques(mosques, clusterDistance)

        clusters.forEach { cluster ->
            val marker = createClusterMarker(mapView, cluster)
            mapView.overlays.add(marker)
        }

        mapView.invalidate()
    }

    private fun clearAllMarkers(mapView: MapView) {
        mapView.overlays.removeAll { it is Marker }
    }

    private fun createClusterMarker(mapView: MapView, cluster: MosqueCluster): Marker {
        return Marker(mapView).apply {
            position = GeoPoint(cluster.centerLatitude, cluster.centerLongitude)

            if (cluster.isSingleMosque) {
                val mosque = cluster.mosques.first()
                title = mosque.name
                snippet = "${mosque.distance} ${context.getString(R.string.kilometer_unit)}"
                icon = ContextCompat.getDrawable(context, R.drawable.marker)
                setOnMarkerClickListener { _, _ ->
                    onMarkerClick(mosque)
                    true
                }
            } else {
                title = "${cluster.count}"
                icon = ClusterIconFactory.createClusterIcon(context, cluster.count)
                setOnMarkerClickListener { _, _ ->
                    val geoPoint = GeoPoint(cluster.centerLatitude, cluster.centerLongitude)
                    mapView.controller.animateTo(geoPoint)
                    mapView.controller.zoomIn()
                    true
                }
            }
        }
    }
}
