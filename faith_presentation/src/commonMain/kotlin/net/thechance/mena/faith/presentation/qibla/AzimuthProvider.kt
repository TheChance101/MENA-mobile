package net.thechance.mena.faith.presentation.qibla

import kotlinx.coroutines.flow.Flow


expect class AzimuthProvider {
    val azimuthFlow: Flow<Float>
    fun startListening()
    fun stopListening()
}
