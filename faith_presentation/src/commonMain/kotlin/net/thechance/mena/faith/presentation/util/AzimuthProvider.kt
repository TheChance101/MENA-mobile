package net.thechance.mena.faith.presentation.util

import kotlinx.coroutines.flow.Flow


expect class AzimuthProvider {
    val azimuthFlow: Flow<Float>
    fun startListening()
    fun stopListening()
}
