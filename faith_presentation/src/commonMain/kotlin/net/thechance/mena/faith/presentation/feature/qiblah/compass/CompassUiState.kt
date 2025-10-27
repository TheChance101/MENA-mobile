package net.thechance.mena.faith.presentation.feature.qiblah.compass

import net.thechance.mena.identity.domain.entity.Address

data class CompassUiState(
    val continuousAzimuth: Float = 0f,
    val qiblahAngleValue: Float = 0f,
    val angleToQiblah: Float = 0f,
    val currentLocationUi: Address? = null,
)
