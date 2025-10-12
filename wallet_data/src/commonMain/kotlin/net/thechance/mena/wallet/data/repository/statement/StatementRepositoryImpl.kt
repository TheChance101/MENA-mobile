package net.thechance.mena.wallet.data.repository.statement

import net.thechance.mena.wallet.data.database.StatementDao
import net.thechance.mena.wallet.data.mapper.toLocal
import net.thechance.mena.wallet.data.mapper.toEntity
import net.thechance.mena.wallet.data.mapper.toStatementWithMetaData
import net.thechance.mena.wallet.data.repository.statement.datasource.local.StatementLocalDataSource
import net.thechance.mena.wallet.data.repository.statement.datasource.remote.StatementRemoteDataSource
import net.thechance.mena.wallet.domain.entity.Statement
import net.thechance.mena.wallet.domain.model.StatementWithMetaData
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import net.thechance.mena.wallet.domain.repository.StatementRepository
import org.koin.core.annotation.Single
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

@Single
class StatementRepositoryImpl(
    private val statementRemoteDataSource: StatementRemoteDataSource,
    private val statementLocalDataSource: StatementLocalDataSource,
    private val statementDao: StatementDao
) : StatementRepository {

    override suspend fun getTransactionPdfWithMetaData(
        filterRequestParams: TransactionFilterParams?
    ): StatementWithMetaData {
        clearExpiredCachedStatements()
        val dto = statementRemoteDataSource.getTransactionPdf(filterRequestParams)
        return dto.toStatementWithMetaData().also {
            cacheRequest(it.byteArray, filterRequestParams)
        }
    }


    override suspend fun insertStatementWithFileName(
        fileName: String,
        statement: Statement
    ) {
        statement.copy(fileName = fileName)
        statementDao.insertStatement(statement.toLocal())

    }

    override suspend fun getStatements(
        page: Int,
        pageSize: Int
    ): List<Statement> {
        val offset = (page - 1) * pageSize
        return statementDao.getAllStatement(limit = pageSize, offset = offset)
            .map { it.toEntity() }
    }

    override suspend fun insertStatement(statement: Statement) {
        statementRemoteDataSource
        statementDao.insertStatement(statement.toLocal())
    }


    override suspend fun deleteStatement(statement: Statement): Boolean {
        return statementDao.deleteStatement(statement.toLocal())
    }

    override suspend fun getStatementById(id: Long): Statement {
        return statementDao.getStatementById(id).toEntity()
    }

    private suspend fun cacheRequest(
        pdf: ByteArray,
        filterRequestParams: TransactionFilterParams?
    ) {
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


    companion object {
        const val EXPIRATION_TIME_INTERVAL_IN_MILLIS = 30_000L
        const val STATEMENT_PATH = "wallet/transactions/statement"
    }
}

