package net.thechance.mena.faith.presentation.utils

import kotlinx.coroutines.flow.Flow


expect class AzimuthProviderImpl : AzimuthProvider {
    override fun startListening(): Flow<Float>
}

interface AzimuthProvider {
    fun startListening(): Flow<Float>
}
