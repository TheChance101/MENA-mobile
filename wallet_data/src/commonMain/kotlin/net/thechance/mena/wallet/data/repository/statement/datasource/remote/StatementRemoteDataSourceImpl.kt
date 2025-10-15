package net.thechance.mena.wallet.data.repository.statement.datasource.remote

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readRawBytes
import net.thechance.mena.wallet.data.database.StatementWithMetaDataDto
import net.thechance.mena.wallet.data.exceptions.safeApiCall
import net.thechance.mena.wallet.data.mapper.toStatementRequest
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import org.koin.core.annotation.Single

@Single(binds = [StatementRemoteDataSource::class])
class StatementRemoteDataSourceImpl(
    private val networkClient: NetworkClient,
) : StatementRemoteDataSource {
    override suspend fun getStatementWithMetaData(filterRequestParams: TransactionFilterParams?): StatementWithMetaDataDto {
        return safeApiCall<HttpResponse> {
            networkClient.get(
                urlString = STATEMENT_PATH,
                block = filterRequestParams?.toStatementRequest() ?: {}
            )
        }.toStatementWithMetaDataDto()
    }

    private suspend fun HttpResponse.toStatementWithMetaDataDto(): StatementWithMetaDataDto {
        return StatementWithMetaDataDto(
            byteArray = readRawBytes(),
            startDate = headers["X-Statement-Start-Date"].orEmpty(),
            endDate = headers["X-Statement-End-Date"].orEmpty(),
            totalInflows = headers["X-Statement-Total-Inflows"]?.toDoubleOrNull() ?: 0.0,
            totalOutflows = headers["X-Statement-Total-Outflows"]?.toDoubleOrNull() ?: 0.0
        )
    }

    companion object {
        const val STATEMENT_PATH = "wallet/transactions/statement"
    }
}