package net.thechance.mena.faith.presentation.feature.qiblah.compass

data class CompassScreenState(
    val azimuth: Float = 0f,
    val qiblahDirection: Float = 0f,
    val currentLocation: Location = Location(),
)

data class Location(
    val cityName: String = "Baghdad, Iraq",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)
