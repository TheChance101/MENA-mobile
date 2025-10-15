package net.thechance.mena.faith.presentation.feature.qiblah.compass

data class CompassScreenState(
    val continuousAzimuth: Float = 0f,
    val qiblahAngleValue: Float = 0f,
    val angleToQiblah: Float = 0f,
    val currentLocation: Location = Location(),
)

data class Location(
    val cityName: String = "Baghdad, Iraq",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)
