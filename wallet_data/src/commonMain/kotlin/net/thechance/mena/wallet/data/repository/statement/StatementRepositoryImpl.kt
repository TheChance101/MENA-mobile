@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.data.repository.statement

import io.ktor.client.statement.HttpResponse
import net.thechance.mena.wallet.data.database.StatementDao
import net.thechance.mena.wallet.data.mapper.toEntity
import net.thechance.mena.wallet.data.mapper.toLocal
import net.thechance.mena.wallet.data.mapper.toStatementEntityList
import net.thechance.mena.wallet.data.mapper.toStatementRequest
import net.thechance.mena.wallet.data.mapper.toStatementWithMetaData
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.data.utils.safeApiCall
import net.thechance.mena.wallet.data.utils.safeCall
import net.thechance.mena.wallet.domain.entity.Statement
import net.thechance.mena.wallet.domain.exceptions.UnknownException
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import net.thechance.mena.wallet.domain.repository.StatementRepository
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Single
class StatementRepositoryImpl(
    private val networkClient: NetworkClient,
    private val statementDao: StatementDao
) : StatementRepository {

    override suspend fun getStatementWithMetadata(
        filterRequestParams: TransactionFilterParams?
    ) = safeApiCall<HttpResponse> {
        networkClient.get(
            urlString = STATEMENT_PATH,
            block = filterRequestParams?.toStatementRequest() ?: {}
        )
    }.toStatementWithMetaData()

    override suspend fun getStatements(page: Int, pageSize: Int): List<Statement> {
        val offset = (page - 1) * pageSize
        return safeCall({ UnknownException("Failed to get statements") }) {
            statementDao.getAllStatement(limit = pageSize, offset = offset).toStatementEntityList()
        }
    }

    override suspend fun insertStatement(statement: Statement) =
        safeCall({ UnknownException("Failed to insert statement") }) {
            statementDao.insertStatement(statement.toLocal())
        }

    override suspend fun deleteStatementById(id: Uuid) =
        safeCall({ UnknownException("Failed to delete statement") }) {
            statementDao.deleteStatementById(id.toString())
        }

    override suspend fun getStatementById(id: Uuid): Statement =
        safeCall({ UnknownException("Failed to get statement") }) {
            statementDao.getStatementById(id.toString()).toEntity()
        }

    companion object {
        const val STATEMENT_PATH = "wallet/transactions/statement"
    }
}
