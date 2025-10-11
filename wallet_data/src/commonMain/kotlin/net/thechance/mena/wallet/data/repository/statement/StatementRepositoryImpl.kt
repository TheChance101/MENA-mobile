package net.thechance.mena.wallet.data.repository.statement

import net.thechance.mena.wallet.data.database.StatementDao
import net.thechance.mena.wallet.data.mapper.toDaoEntity
import net.thechance.mena.wallet.data.mapper.toDomainEntity
import net.thechance.mena.wallet.data.repository.statement.datasource.local.StatementLocalDataSource
import net.thechance.mena.wallet.data.repository.statement.datasource.remote.StatementRemoteDataSource
import net.thechance.mena.wallet.domain.entity.Statement
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import net.thechance.mena.wallet.domain.repository.StatementRepository
import org.koin.core.annotation.Single
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime
import kotlin.uuid.Uuid

@Single
class StatementRepositoryImpl(
    private val statementRemoteDataSource: StatementRemoteDataSource,
    private val statementLocalDataSource: StatementLocalDataSource,
    private val statementDao: StatementDao
) : StatementRepository {

    override suspend fun getTransactionsPdf(
        filterRequestParams: TransactionFilterParams?,
    ): ByteArray {
        clearExpiredCachedStatements()
        return statementLocalDataSource.getStatement(filterRequestParams.key())
            ?: statementRemoteDataSource.getTransactionPdf(filterRequestParams)
                .also { pdf -> cacheRequest(pdf, filterRequestParams) }
    }

    override suspend fun getStatements(
        page: Int,
        pageSize: Int
    ): List<Statement> {
        val offset = (page - 1) * pageSize
        return statementDao.getAllStatement(limit = pageSize, offset = offset).map{it.toDomainEntity()}
    }

    override suspend fun insertStatement(statement: Statement) {
        statementDao.insertStatement(statement.toDaoEntity())
    }

    override suspend fun deleteStatement(statement: Statement): Boolean {
        return statementDao.deleteStatement(statement.toDaoEntity())
    }

    override suspend fun getStatementById(id: Long): Statement {
       return statementDao.getStatementById(id).toDomainEntity()
    }

    private suspend fun cacheRequest(pdf: ByteArray, filterRequestParams: TransactionFilterParams?) {
        statementLocalDataSource.saveStatement(
            pdf.toCachedTransactionsPdfDto(
                filterRequestParams.key()
            )
        )
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun clearExpiredCachedStatements() {
        statementLocalDataSource.clearExpiredStatements(
            Clock.System.now() - EXPIRATION_TIME_INTERVAL_IN_MILLIS.milliseconds
        )
    }


   private companion object {
        const val EXPIRATION_TIME_INTERVAL_IN_MILLIS = 30_000L
        const val STATEMENT_PATH = "wallet/transactions/statement"
    }
}

