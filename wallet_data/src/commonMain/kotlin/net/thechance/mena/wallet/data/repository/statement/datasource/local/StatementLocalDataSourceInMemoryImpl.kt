package net.thechance.mena.wallet.data.repository.statement.datasource.local

import net.thechance.mena.wallet.data.repository.statement.CachedTransactionsPdfDto
import org.koin.core.annotation.Single
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Single(binds = [StatementLocalDataSource::class])
class StatementLocalDataSourceInMemoryImpl: StatementLocalDataSource {
    val statements = mutableListOf<CachedTransactionsPdfDto>()

    override suspend fun saveStatement(statement: CachedTransactionsPdfDto) {
        statements.add(statement)
    }

    override suspend fun getStatement(key: String): ByteArray? {
        return statements.find { it.key == key }?.pdf
    }

    override suspend fun clearStatement(key: String) {
        statements.removeAll { it.key == key }
    }

    override suspend fun clearAllStatements() {
        statements.clear()
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun clearExpiredStatements(expiredTime: Instant) {
        statements.removeAll { it.timestamp < expiredTime }
    }
}