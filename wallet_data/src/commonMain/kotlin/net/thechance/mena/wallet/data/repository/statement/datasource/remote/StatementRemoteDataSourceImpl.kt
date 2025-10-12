package net.thechance.mena.wallet.data.repository.statement.datasource.remote

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readRawBytes
import net.thechance.mena.wallet.data.database.LocalStatement
import net.thechance.mena.wallet.data.database.StatementWithMetaDataDto
import net.thechance.mena.wallet.data.exceptions.safeApiCall
import net.thechance.mena.wallet.data.mapper.toStatementRequest
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.data.repository.statement.StatementRepositoryImpl.Companion.STATEMENT_PATH
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import org.koin.core.annotation.Single

@Single(binds = [StatementRemoteDataSource::class])
class StatementRemoteDataSourceImpl(
    private val networkClient: NetworkClient,
) : StatementRemoteDataSource {
    override suspend fun getTransactionPdf(filterRequestParams: TransactionFilterParams?): StatementWithMetaDataDto {
        val response = safeApiCall<HttpResponse> {
            networkClient.get(
                urlString = STATEMENT_PATH,
                block = filterRequestParams?.toStatementRequest() ?: {}
            )
        }
        val statement = extractStatementInfoFromHeaders(response)
        return StatementWithMetaDataDto(
            byteArray = response.readRawBytes(),
            startDate = statement.startDate,
            endDate = statement.endDate,
            totalInflows = statement.totalInflows,
            totalOutflows = statement.totalOutflows
        )
    }


    fun extractStatementInfoFromHeaders(response: HttpResponse): LocalStatement {
        return LocalStatement(
            totalInflows = response.headers["X-Statement-Total-Inflows"]?.toDoubleOrNull() ?: 0.0,
            totalOutflows = response.headers["X-Statement-Total-Outflows"]?.toDoubleOrNull() ?: 0.0,
            startDate = response.headers["X-Statement-Start-Date"].orEmpty(),
            endDate = response.headers["X-Statement-End-Date"].orEmpty(),
            fileName = ""

        )
    }

}