package net.thechance.mena.faith.presentation.map

import net.thechance.mena.faith.presentation.feature.mosque.Coordinate
import net.thechance.mena.faith.presentation.feature.mosque.MosqueUiState
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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
            val distance = calculateDistance(
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

    private fun calculateDistance(
        firstCoordinate: Coordinate,
        secondCoordinate: Coordinate
    ): Double {
        val latRadians1 = firstCoordinate.latitude * PI / 180.0
        val longRadians1 = firstCoordinate.longitude * PI / 180.0
        val latRadians2 = secondCoordinate.latitude * PI / 180.0
        val longRadians2 = secondCoordinate.longitude * PI / 180.0

        val deltaLat = latRadians2 - latRadians1
        val deltaLong = longRadians2 - longRadians1

        val haversineOfHalfAngle = sin(deltaLat * 0.5) * sin(deltaLat * 0.5) +
                cos(latRadians1) * cos(latRadians2) *
                sin(deltaLong * 0.5) * sin(deltaLong * 0.5)
        val angularDistanceInRadians =
            2.0 * atan2(y = sqrt(haversineOfHalfAngle), x = sqrt(1.0 - haversineOfHalfAngle))

        return MapConstants.EARTH_RADIUS_METERS * angularDistanceInRadians
    }
}

data class MosqueCluster(val mosques: List<MosqueUiState>) {
    val isSingleMosque: Boolean = mosques.size == 1
    val count: Int = mosques.size
    val centerLatitude: Double = mosques.map { it.coordinate.latitude }.average()
    val centerLongitude: Double = mosques.map { it.coordinate.longitude }.average()
}