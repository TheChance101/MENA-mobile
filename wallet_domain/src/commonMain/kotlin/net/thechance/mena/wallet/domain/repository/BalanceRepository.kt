package net.thechance.mena.wallet.domain.repository

import kotlinx.coroutines.flow.Flow

interface BalanceRepository {
    suspend fun getBalance(): Double
    fun observeBalance(): Flow<Double?>
}