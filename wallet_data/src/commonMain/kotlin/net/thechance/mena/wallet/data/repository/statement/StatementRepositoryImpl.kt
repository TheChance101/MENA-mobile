package net.thechance.mena.wallet.data.repository.statement

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
        return statementDao.getAllStatement(limit = pageSize, offset = offset)
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

