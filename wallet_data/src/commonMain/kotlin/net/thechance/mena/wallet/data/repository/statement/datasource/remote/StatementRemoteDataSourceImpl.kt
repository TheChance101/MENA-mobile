package net.thechance.mena.wallet.data.repository.statement.datasource.remote

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readRawBytes
import net.thechance.mena.wallet.data.database.Statement
import net.thechance.mena.wallet.data.database.StatementDao
import net.thechance.mena.wallet.data.exceptions.safeApiCall
import net.thechance.mena.wallet.data.mapper.toStatementRequest
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.data.repository.statement.StatementRepositoryImpl.Companion.STATEMENT_PATH
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import org.koin.core.annotation.Single

@Single(binds = [StatementRemoteDataSource::class])
class StatementRemoteDataSourceImpl(
    private val networkClient: NetworkClient,
    private val statementDao : StatementDao
): StatementRemoteDataSource {
    override suspend fun getTransactionPdf(filterRequestParams: TransactionFilterParams?): ByteArray {
        val response =safeApiCall<HttpResponse> {
            networkClient.get(
                urlString = STATEMENT_PATH,
                block = filterRequestParams?.toStatementRequest() ?: {}
            )
        }
        statementDao.insertStatement(extractStatementInfoFromHeaders(response))
        return response.readRawBytes()


    }
}
private fun extractStatementInfoFromHeaders(response : HttpResponse): Statement{
    return Statement(
        totalInflows = response.headers["X-Statement-Total-Inflows"]?.toDoubleOrNull() ?: 0.0,
        totalOutflows = response.headers["X-Statement-Total-Outflows"]?.toDoubleOrNull() ?: 0.0,
        startDate = response.headers["X-Statement-Start-Date"].orEmpty(),
        endDate = response.headers["X-Statement-End-Date"].orEmpty()
    )

}