package net.thechance.mena.faith.presentation.map

import net.thechance.mena.faith.presentation.utils.MapMarker
import org.osmdroid.util.GeoPoint

class MarkerClusterer {

    fun clusterMarkers(
        markers: List<MapMarker>,
        clusterDistance: Double
    ): List<MarkerCluster> {
        val clusters = mutableListOf<MutableList<MapMarker>>()

        markers.forEach { marker ->
            val existingCluster = findNearbyCluster(marker, clusters, clusterDistance)

            if (existingCluster != null) {
                existingCluster.add(marker)
            } else {
                clusters.add(mutableListOf(marker))
            }
        }

        return clusters.map { MarkerCluster(it) }
    }

    private fun findNearbyCluster(
        marker: MapMarker,
        clusters: List<MutableList<MapMarker>>,
        maxDistance: Double
    ): MutableList<MapMarker>? {
        return clusters.firstOrNull { cluster ->
            val representative = cluster.first()
            val distance = MapUtils.calculateDistance(
                GeoPoint(marker.latitude, marker.longitude),
                GeoPoint(representative.latitude, representative.longitude)
            )
            distance <= maxDistance
        }
    }
}

data class MarkerCluster(val markers: List<MapMarker>) {
    val isSingleMarker: Boolean = markers.size == 1
    val count: Int = markers.size
    val centerLatitude: Double = markers.map { it.latitude }.average()
    val centerLongitude: Double = markers.map { it.longitude }.average()
}