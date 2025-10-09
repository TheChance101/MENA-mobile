package net.thechance.mena.faith.presentation.feature.qiblah.compass

data class CompassScreenState(
    val azimuth: Float = 0f,
    val qiblahDirection: Float = 270f,
    val currentLocation: Location? = Location(),
)

data class Location(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)
