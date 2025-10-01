package net.thechance.mena.wallet.data.repository.statement

import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readRawBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.thechance.mena.wallet.data.exceptions.safeApiCall
import net.thechance.mena.wallet.data.network_client.NetworkClient
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

    override suspend fun getStatement(): ByteArray {
        return safeApiCall<HttpResponse>{
            networkClient.get(STATEMENT_PATH) {
                header(HttpHeaders.Accept, ContentType.Application.Pdf)
            }
        }.readRawBytes().also { statement ->
            lastStatement = statement
            deleteStatementInAnHour()
        }
    }

    override suspend fun getLastStatement(): ByteArray? {
        return lastStatement
    }

    private fun deleteStatementInAnHour() {
        coroutineScope.launch {
            delay(HOUR_IN_MILLIS)
            lastStatement = null
        }
    }

    private companion object {
        const val HOUR_IN_MILLIS = 3_600_000L
        const val STATEMENT_PATH = "wallet/transactions/statement"
    }
}

