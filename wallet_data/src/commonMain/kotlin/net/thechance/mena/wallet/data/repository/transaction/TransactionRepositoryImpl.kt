package net.thechance.mena.wallet.data.repository.transaction

import io.ktor.client.request.setBody
import net.thechance.mena.wallet.data.dto.remote.FirstTransactionDateDto
import net.thechance.mena.wallet.data.dto.remote.PagedResponse
import net.thechance.mena.wallet.data.dto.remote.PendingTransactionRequestBody
import net.thechance.mena.wallet.data.dto.remote.TransactionDto
import net.thechance.mena.wallet.data.mapper.toEntity
import net.thechance.mena.wallet.data.mapper.toEntityList
import net.thechance.mena.wallet.data.mapper.toRequest
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.data.utils.safeApiCall
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Single
class TransactionRepositoryImpl(
    private val networkClient: NetworkClient
) : TransactionRepository {
    override suspend fun getTransactionHistory(
        page: Int,
        pageSize: Int,
        transactionFilterParams: TransactionFilterParams?
    ) = safeApiCall<PagedResponse<TransactionDto>> {
        networkClient.get(
            urlString = TRANSACTION_PATH,
            requestBuilder = transactionFilterParams?.toRequest(page = page, pageSize = pageSize)
                ?: {}
        )
    }.toEntityList(TransactionDto::toEntity)

    override suspend fun getTransactionById(transactionId: Uuid) = safeApiCall<TransactionDto> {
        networkClient.get(getTransactionByIdPath(transactionId))
    }.toEntity()

    override suspend fun getFirstTransactionDate() = safeApiCall<FirstTransactionDateDto> {
        networkClient.get(FIRST_TRANSACTION_DATE_PATH)
    }.firstTransactionDate

    override suspend fun addPendingTransaction(receiverId: Uuid, amount: Double): Uuid {
        return safeApiCall<Uuid> {
            networkClient.post(ADD_TRANSACTION_PATH) {
                setBody(
                    PendingTransactionRequestBody(
                        amount = amount,
                        receiverId = receiverId.toString()
                    )
                )
            }
        }
    }

    override suspend fun submitTransaction(transactionId: Uuid) = safeApiCall<Unit> {
        networkClient.post(getSubmitTransactionPath(transactionId))
    }


    private companion object {
        const val TRANSACTION_PATH = "wallet/transactions"
        const val PAYMENT_PATH = "wallet/payments"
        const val FIRST_TRANSACTION_DATE_PATH = "$TRANSACTION_PATH/first-date"
        const val ADD_TRANSACTION = "/p2p/initiate"
        const val ADD_TRANSACTION_PATH = "$TRANSACTION_PATH$ADD_TRANSACTION"

        fun getTransactionByIdPath(transactionId: Uuid) = "$TRANSACTION_PATH/$transactionId"
        fun getSubmitTransactionPath(transactionId: Uuid) =
            "$PAYMENT_PATH/$transactionId/submit"
    }
}