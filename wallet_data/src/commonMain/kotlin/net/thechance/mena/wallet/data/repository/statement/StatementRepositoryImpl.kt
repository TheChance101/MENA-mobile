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
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Single
class StatementRepositoryImpl(
    private val networkClient: NetworkClient,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : StatementRepository {

    @Volatile
    private var cachedRequest: CachedRequest? = null

    @Volatile
    private var clearCacheJob: Job? = null

    override suspend fun getTransactionsPdf(
        filterRequestParams: TransactionFilterParams?,
    ): ByteArray {
        return getCachedPdf(filterRequestParams)
            ?: fetchTransactionPdf(filterRequestParams)
                .also { pdf -> cacheRequest(pdf, filterRequestParams) }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun getStoredTransactionsPdf(): ByteArray? {
        return cachedRequest
            ?.takeIf {
                it.timestamp.plus(EXPIRATION_TIME_INTERVAL_IN_MILLIS.milliseconds) > Clock.System.now()
            }
            ?.pdf
    }

    private suspend fun fetchTransactionPdf(filterRequestParams: TransactionFilterParams?): ByteArray {
        return safeApiCall<HttpResponse> {
            networkClient.get(
                urlString = STATEMENT_PATH,
                block = filterRequestParams?.toStatementRequest() ?: {}
            )
        }.readRawBytes()
    }

    @OptIn(ExperimentalTime::class)
    private fun getCachedPdf(filterRequestParams: TransactionFilterParams? = null): ByteArray? {
        return cachedRequest
            ?.takeIf {
                it.filterRequestParams == filterRequestParams
                        && it.timestamp.plus(EXPIRATION_TIME_INTERVAL_IN_MILLIS.milliseconds) > Clock.System.now()

            }?.pdf
    }

    @OptIn(ExperimentalTime::class)
    private fun cacheRequest(pdf: ByteArray, filterRequestParams: TransactionFilterParams?) {
        clearCacheJob?.cancel()
        cachedRequest = CachedRequest(
            pdf = pdf,
            filterRequestParams = filterRequestParams
        )
        clearCacheJob = coroutineScope.launch {
            delay(EXPIRATION_TIME_INTERVAL_IN_MILLIS)
            cachedRequest = null
        }
    }

    @OptIn(ExperimentalTime::class)
    private class CachedRequest(
        val filterRequestParams: TransactionFilterParams?,
        val pdf: ByteArray,
        val timestamp: Instant = Clock.System.now()
    )

    private companion object {
        const val EXPIRATION_TIME_INTERVAL_IN_MILLIS = 3_600_000L
        const val STATEMENT_PATH = "wallet/transactions/statement"
    }
}

