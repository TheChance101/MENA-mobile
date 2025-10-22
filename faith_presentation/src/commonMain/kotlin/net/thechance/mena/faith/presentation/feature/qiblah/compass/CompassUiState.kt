package net.thechance.mena.faith.presentation.feature.qiblah.compass

import net.thechance.mena.faith.domain.entity.Location

data class CompassUiState(
    val continuousAzimuth: Float = 0f,
    val qiblahAngleValue: Float = 0f,
    val angleToQiblah: Float = 0f,
    val currentLocationUi: LocationUi = LocationUi(),
)

data class LocationUi(
    val cityName: String = "Cairo, Egypt",
    val latitude: Double = 30.0594628,
    val longitude: Double = 31.1760627,
)

fun LocationUi.toLocation() = Location(
    longitude = longitude,
    latitude = latitude
)