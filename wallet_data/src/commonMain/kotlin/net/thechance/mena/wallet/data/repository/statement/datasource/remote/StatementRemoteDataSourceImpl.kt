package net.thechance.mena.wallet.data.repository.statement.datasource.remote

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readRawBytes
import net.thechance.mena.wallet.data.exceptions.safeApiCall
import net.thechance.mena.wallet.data.mapper.toStatementRequest
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.data.repository.statement.StatementRepositoryImpl.Companion.STATEMENT_PATH
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import org.koin.core.annotation.Single

@Single(binds = [StatementRemoteDataSource::class])
class StatementRemoteDataSourceImpl(
    private val networkClient: NetworkClient
): StatementRemoteDataSource {
    override suspend fun getTransactionPdf(filterRequestParams: TransactionFilterParams?): ByteArray {
        return safeApiCall<HttpResponse> {
            networkClient.get(
                urlString = STATEMENT_PATH,
                block = filterRequestParams?.toStatementRequest() ?: {}
            )
        }.readRawBytes()
    }
}