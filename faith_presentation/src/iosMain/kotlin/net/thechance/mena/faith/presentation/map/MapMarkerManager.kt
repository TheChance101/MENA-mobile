package net.thechance.mena.faith.presentation.map

import kotlinx.cinterop.ExperimentalForeignApi
import net.thechance.mena.faith.presentation.feature.mosque.MosqueUiState
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation

class MapMarkerManager() {

    fun updateMarkers(
        mapView: MKMapView,
        mosques: List<MosqueUiState>,
        zoomLevel: Double,
    ) {
        clearAllMarkers(mapView)

        if (mosques.isEmpty()) return

        val clusterer = MosqueClusterer()

        val clusterDistance = getClusterDistance(zoomLevel)

        val clusters = clusterer.clusterMosques(mosques, clusterDistance)

        clusters.forEach { cluster ->
            val annotation = createClusterMarker(cluster = cluster)
            mapView.addAnnotation(annotation)
        }
    }

    private fun clearAllMarkers(mapView: MKMapView) {
        mapView.removeAnnotations(mapView.annotations)
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun createClusterMarker(
        cluster: MosqueCluster,
    ): MKPointAnnotation {
        val annotation = MKPointAnnotation()

        if (cluster.isSingleMosque) {
            val mosque = cluster.mosques.first()
            val coordinate = CLLocationCoordinate2DMake(
                mosque.coordinate.latitude,
                mosque.coordinate.longitude
            )
            annotation.setCoordinate(coordinate)
            annotation.setTitle(mosque.name)
        } else {
            val coordinate = CLLocationCoordinate2DMake(
                cluster.centerLatitude,
                cluster.centerLongitude
            )
            annotation.setCoordinate(coordinate)
            annotation.setTitle("${cluster.count}")
        }
        return annotation
    }

    private fun getClusterDistance(zoomLevel: Double): Double {
        return when {
            zoomLevel < 4.0 -> 100000.0
            zoomLevel < 6.0 -> 50000.0
            zoomLevel < 8.0 -> 20000.0
            zoomLevel < 10.0 -> 8000.0
            zoomLevel < 12.0 -> 3000.0
            zoomLevel < 14.0 -> 1000.0
            zoomLevel < 15.0 -> 500.0
            zoomLevel < 16.0 -> 200.0
            else -> 0.0
        }
    }
}