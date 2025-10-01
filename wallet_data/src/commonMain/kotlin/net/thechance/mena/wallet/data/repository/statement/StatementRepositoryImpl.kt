package net.thechance.mena.wallet.data.repository.statement

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readRawBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.thechance.mena.wallet.data.exceptions.safeApiCall
import net.thechance.mena.wallet.data.mapper.toStatementRequest
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import net.thechance.mena.wallet.domain.repository.StatementRepository
import org.koin.core.annotation.Single
import kotlin.concurrent.Volatile

@Single
class StatementRepositoryImpl(
    private val networkClient: NetworkClient,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
): StatementRepository {

    @Volatile
    private var lastStatement: ByteArray? = null
    @Volatile
    private var deleteStatementJob: Job? = null

    override suspend fun getStatement(
        filterRequestParams: TransactionFilterParams?
    ): ByteArray {
        return safeApiCall<HttpResponse>{
            networkClient.get(
                urlString = STATEMENT_PATH,
                block = filterRequestParams?.toStatementRequest() ?: {}
            )
        }.readRawBytes().also { statement ->
            lastStatement = statement
            deleteStatementInAnHour()
        }
    }

    override suspend fun getLastStatement(): ByteArray? {
        return lastStatement
    }

    private fun deleteStatementInAnHour() {
        deleteStatementJob?.cancel()
        deleteStatementJob = coroutineScope.launch {
            delay(HOUR_IN_MILLIS)
            lastStatement = null
        }
    }

    private companion object {
        const val HOUR_IN_MILLIS = 3_600_000L
        const val STATEMENT_PATH = "wallet/transactions/statement"
    }
}

