package net.thechance.mena.wallet.domain.repository

interface StatementRepository {
    suspend fun getStatement(): ByteArray
    suspend fun getLastStatement(): ByteArray?
}