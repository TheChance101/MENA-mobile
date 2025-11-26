@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.data.repository.statement

import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.first
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.wallet.data.database.StatementDao
import net.thechance.mena.wallet.data.mapper.toEntity
import net.thechance.mena.wallet.data.mapper.toLocal
import net.thechance.mena.wallet.data.mapper.toStatementEntityList
import net.thechance.mena.wallet.data.mapper.toStatementRequest
import net.thechance.mena.wallet.data.mapper.toStatementWithMetaData
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.data.utils.safeApiCall
import net.thechance.mena.wallet.domain.entity.Statement
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import net.thechance.mena.wallet.domain.repository.StatementRepository
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Single
class StatementRepositoryImpl(
    private val networkClient: NetworkClient,
    @Provided private val userRepository: UserRepository,
    private val statementDao: StatementDao
) : StatementRepository {

    override suspend fun getStatementWithMetadata(
        filterRequestParams: TransactionFilterParams?
    ) = safeApiCall<HttpResponse> {
        networkClient.get(
            urlString = STATEMENT_PATH,
            requestBuilder = filterRequestParams?.toStatementRequest() ?: {}
        )
    }.toStatementWithMetaData()

    override suspend fun getStatements(page: Int, pageSize: Int): List<Statement> {
        val userId = userRepository.getUser().first()?.id.toString()
        val offset = (page - 1) * pageSize
        return statementDao.getAllStatement(userId = userId, limit = pageSize, offset = offset)
            .toStatementEntityList()
    }

    override suspend fun insertStatement(statement: Statement) {
        val userId = userRepository.getUser().first()?.id.toString()
        statementDao.insertStatement(statement.toLocal(userId))
    }

    override suspend fun deleteStatementById(id: Uuid) =
        statementDao.deleteStatementById(id.toString())

    override suspend fun getStatementById(id: Uuid): Statement =
        statementDao.getStatementById(id.toString()).toEntity()

    companion object {
        const val STATEMENT_PATH = "wallet/transactions/statement"
    }
}
