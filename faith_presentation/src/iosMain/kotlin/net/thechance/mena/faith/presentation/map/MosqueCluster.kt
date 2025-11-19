package net.thechance.mena.faith.presentation.map

import net.thechance.mena.faith.presentation.feature.mosque.Coordinate
import net.thechance.mena.faith.presentation.feature.mosque.MosqueUiState

class MosqueClusterer {

    fun clusterMosques(
        mosques: List<MosqueUiState>,
        clusterDistance: Double
    ): List<MosqueCluster> {
        val clusters = mutableListOf<MutableList<MosqueUiState>>()
        mosques.forEach { mosque ->
            findNearbyCluster(
                mosque = mosque,
                clusters = clusters,
                maxDistance = clusterDistance
            )?.add(mosque) ?: clusters.add(mutableListOf(mosque))
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
                firstCoordinate = Coordinate(
                    latitude = mosque.coordinate.latitude,
                    longitude = mosque.coordinate.longitude
                ),
                secondCoordinate = Coordinate(
                    latitude = representative.coordinate.latitude,
                    longitude = representative.coordinate.longitude
                )
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