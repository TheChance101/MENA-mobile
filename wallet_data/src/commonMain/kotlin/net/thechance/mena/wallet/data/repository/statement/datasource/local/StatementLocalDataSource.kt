package net.thechance.mena.wallet.data.repository.statement.datasource.local

import net.thechance.mena.wallet.data.repository.statement.CachedTransactionsPdfDto
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

interface StatementLocalDataSource {
    suspend fun saveStatement(statement: CachedTransactionsPdfDto)
    suspend fun getStatement(key: String): ByteArray?
    suspend fun clearStatement(key: String)
    suspend fun clearAllStatements()
    @OptIn(ExperimentalTime::class)
    suspend fun clearExpiredStatements(expiredTime: Instant)
}