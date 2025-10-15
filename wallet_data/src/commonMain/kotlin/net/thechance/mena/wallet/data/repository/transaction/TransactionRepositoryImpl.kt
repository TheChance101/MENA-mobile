package net.thechance.mena.wallet.data.repository.transaction

import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import kotlinx.datetime.LocalDate
import net.thechance.mena.wallet.data.dto.FirstTransactionDateDto
import net.thechance.mena.wallet.data.dto.PagedTransactionResponseDto
import net.thechance.mena.wallet.data.dto.PendingTransactionRequestBody
import net.thechance.mena.wallet.data.dto.TransactionDto
import net.thechance.mena.wallet.data.dto.TransactionReceiverDto
import net.thechance.mena.wallet.data.exceptions.safeApiCall
import net.thechance.mena.wallet.data.mapper.toEntity
import net.thechance.mena.wallet.data.mapper.toRequest
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.model.TransactionReceiver
import net.thechance.mena.wallet.domain.model.PendingTransactionType
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
    ): List<Transaction> {
        return safeApiCall<PagedTransactionResponseDto> {
            networkClient.get(
                urlString = TRANSACTION_PATH,
                block = transactionFilterParams?.toRequest(page = page, pageSize = pageSize) ?: {}
            )
        }.transactions.orEmpty().map { it.toEntity() }
    }

    override suspend fun getTransactionById(transactionId: Uuid): Transaction {
        return safeApiCall<TransactionDto> {
            networkClient.get("$TRANSACTION_PATH/$transactionId")
        }.toEntity()
    }

    override suspend fun getFirstTransactionDate(): LocalDate? {
        return safeApiCall<FirstTransactionDateDto> {
            networkClient.get(FIRST_TRANSACTION_DATE_PATH)
        }.firstTransactionDate
    }

    override suspend fun addPendingTransaction(
        pendingTransactionType: PendingTransactionType,
        receiverId: Uuid,
        amount: Double
    ): Uuid {
        return safeApiCall<Uuid> {
            networkClient.post("$TRANSACTION_PATH$ADD_TRANSACTION") {
                setBody(
                    PendingTransactionRequestBody(
                        amount = amount,
                        receiverId = receiverId.toString(),
                        type = pendingTransactionType.name
                    )
                )
            }
        }
    }

    override suspend fun getTransactionReceiver(transactionId: Uuid): TransactionReceiver {
        return safeApiCall<TransactionReceiverDto> {
            networkClient.get("${TRANSACTION_PATH}$RECEIVER_DETAILS"){
                parameter(TRANSACTION_ID_PARAM, transactionId)
            }
        }.toEntity()
    }

    private companion object {
        const val TRANSACTION_PATH = "wallet/transactions"
        const val FIRST_TRANSACTION_DATE_PATH = "$TRANSACTION_PATH/first-date"
        const val ADD_TRANSACTION = "/add"
        const val RECEIVER_DETAILS = "/receiver-details"
        const val TRANSACTION_ID_PARAM = "transactionId"
    }

}