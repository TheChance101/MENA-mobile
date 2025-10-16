package net.thechance.mena.wallet.data.repository.statement

import net.thechance.mena.wallet.data.database.LocalStatement
import net.thechance.mena.wallet.data.database.StatementDao
import net.thechance.mena.wallet.data.mapper.toEntity
import net.thechance.mena.wallet.data.mapper.toLocal
import net.thechance.mena.wallet.data.mapper.toStatementWithMetaData
import net.thechance.mena.wallet.data.repository.statement.datasource.remote.StatementRemoteDataSource
import net.thechance.mena.wallet.domain.entity.Statement
import net.thechance.mena.wallet.domain.model.StatementWithMetaData
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import net.thechance.mena.wallet.domain.repository.StatementRepository
import org.koin.core.annotation.Single
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Single
class StatementRepositoryImpl(
    private val statementRemoteDataSource: StatementRemoteDataSource,
    private val statementDao: StatementDao
) : StatementRepository {

    override suspend fun getStatementWithMetadata(
        filterRequestParams: TransactionFilterParams?
    ): StatementWithMetaData {
        return statementRemoteDataSource
            .getStatementWithMetaData(filterRequestParams)
            .toStatementWithMetaData()
    }

    override suspend fun getStatements(
        page: Int,
        pageSize: Int
    ): List<Statement> {
        val offset = (page - 1) * pageSize
        return getFakeStatements()
            .map { it.toEntity() }
    }

    override suspend fun insertStatement(statement: Statement) {
        statementDao.insertStatement(statement.toLocal())
    }


    override suspend fun deleteStatementById(id: Long) {
        statementDao.deleteStatementById(id)
    }

    override suspend fun getStatementById(id: Long): Statement {
        return statementDao.getStatementById(id).toEntity()
    }
}
@OptIn(ExperimentalTime::class)
private fun getFakeStatements(): List<LocalStatement> = listOf(
    LocalStatement(
        id = 1L,
        startDate = "2025-01-01",
        endDate = "2025-01-31",
        totalInflows = 15000.50,
        totalOutflows = 8500.25,
        fileName = "statement_january_2025.pdf",
        createdAt = Clock.System.now().toEpochMilliseconds(),
    ),
    LocalStatement(
        id = 2L,
        startDate = "2025-02-01",
        endDate = "2025-02-28",
        totalInflows = 18500.75,
        totalOutflows = 9200.40,
        fileName = "statement_february_2025.pdf",
        createdAt = Clock.System.now().toEpochMilliseconds(),
    ),
    LocalStatement(
        id = 3L,
        startDate = "2025-03-01",
        endDate = "2025-03-31",
        totalInflows = 22000.00,
        totalOutflows = 11000.80,
        fileName = "statement_march_2025.pdf",
        createdAt =Clock.System.now().toEpochMilliseconds(),
    ),
    LocalStatement(
        id = 4L,
        startDate = "2025-04-01",
        endDate = "2025-04-30",
        totalInflows = 19750.30,
        totalOutflows = 10500.60,
        fileName = "statement_april_2025.pdf",
        createdAt =Clock.System.now().toEpochMilliseconds(),
    ),
    LocalStatement(
        id = 5L,
        startDate = "2025-05-01",
        endDate = "2025-05-31",
        totalInflows = 21300.90,
        totalOutflows = 12200.45,
        fileName = "statement_may_2025.pdf",
        createdAt =Clock.System.now().toEpochMilliseconds(),
    ),
    LocalStatement(
        id = 6L,
        startDate = "2025-06-01",
        endDate = "2025-06-30",
        totalInflows = 17800.65,
        totalOutflows = 9800.20,
        fileName = "statement_june_2025.pdf",
        createdAt = Clock.System.now().toEpochMilliseconds(),
    )
)
