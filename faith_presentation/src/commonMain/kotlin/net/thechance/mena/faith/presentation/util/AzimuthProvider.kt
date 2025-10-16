package net.thechance.mena.faith.presentation.util

import kotlinx.coroutines.flow.Flow


expect class AzimuthProvider {
    fun startListening(): Flow<Float>
}
