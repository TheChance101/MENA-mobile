package net.thechance.mena.faith.presentation.feature.qiblah.compass

data class CompassUiState(
    val continuousAzimuth: Float = 0f,
    val qiblahAngleValue: Float = 0f,
    val angleToQiblah: Float = 0f,
    val address: String = ""

)
