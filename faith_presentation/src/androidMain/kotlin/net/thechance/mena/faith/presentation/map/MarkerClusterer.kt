package net.thechance.mena.faith.presentation.map

import net.thechance.mena.faith.presentation.feature.mosque.MosqueUiState
import org.osmdroid.util.GeoPoint

class MosqueClusterer {

    fun clusterMosques(
        mosques: List<MosqueUiState>,
        clusterDistance: Double
    ): List<MosqueCluster> {
        val clusters = mutableListOf<MutableList<MosqueUiState>>()

        mosques.forEach { mosque ->
            val existingCluster = findNearbyCluster(mosque, clusters, clusterDistance)

            if (existingCluster != null) {
                existingCluster.add(mosque)
            } else {
                clusters.add(mutableListOf(mosque))
            }
        }

        return clusters.map { MosqueCluster(it) }
    }

    private fun findNearbyCluster(
        mosque: MosqueUiState,
        clusters: List<MutableList<MosqueUiState>>,
        maxDistance: Double
    ): MutableList<MosqueUiState>? {
        return clusters.firstOrNull { cluster ->
            val representative = cluster.first()
            val distance = MapUtils.calculateDistance(
                GeoPoint(mosque.coordinate.latitude, mosque.coordinate.longitude),
                GeoPoint(representative.coordinate.latitude, representative.coordinate.longitude)
            )
            distance <= maxDistance
        }
    }
}

data class MosqueCluster(val mosques: List<MosqueUiState>) {
    val isSingleMosque: Boolean = mosques.size == 1
    val count: Int = mosques.size
    val centerLatitude: Double = mosques.map { it.coordinate.latitude }.average()
    val centerLongitude: Double = mosques.map { it.coordinate.longitude }.average()
}
