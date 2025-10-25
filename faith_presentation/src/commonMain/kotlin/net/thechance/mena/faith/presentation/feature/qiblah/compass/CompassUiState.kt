package net.thechance.mena.faith.presentation.feature.qiblah.compass

import net.thechance.mena.identity.domain.entity.Address
import kotlin.uuid.ExperimentalUuidApi

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

@OptIn(ExperimentalUuidApi::class)
fun LocationUi.toLocation() = Address(
    id = TODO(),
    addressLine = TODO(),
    addressType = TODO(),
    longitude = longitude,
    latitude = latitude,
    )