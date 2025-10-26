@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.faith.presentation.feature.qiblah.compass

import net.thechance.mena.identity.domain.entity.Address
import net.thechance.mena.identity.domain.entity.AddressType
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class CompassUiState(
    val continuousAzimuth: Float = 0f,
    val qiblahAngleValue: Float = 0f,
    val angleToQiblah: Float = 0f,
    val currentLocationUi: LocationUi = LocationUi(),
)

data class LocationUi(
    val id: Uuid = Uuid.random(),
    val addressDetails: String = "Cairo, Egypt",
    val addressType: AddressType = AddressType.Home,
    val latitude: Double = 30.0594628,
    val longitude: Double = 31.1760627,
)

fun LocationUi.toAddress() = Address(
    id = id,
    addressLine = addressDetails,
    addressType = addressType,
    longitude = longitude,
    latitude = latitude,
)

fun Address.toLocationUi() = LocationUi(
    id = id ?: Uuid.random(),
    addressDetails = addressLine,
    addressType = addressType,
    longitude = longitude,
    latitude = latitude,
)

