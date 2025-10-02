package net.thechance.mena.wallet.data.repository.balance

import net.thechance.mena.wallet.data.dto.PagedTransactionResponseDto
import net.thechance.mena.wallet.data.dto.TransactionDto
import net.thechance.mena.wallet.data.exceptions.safeApiCall
import net.thechance.mena.wallet.data.mapper.toEntity
import net.thechance.mena.wallet.data.mapper.toParameters
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.domain.entity.Transaction
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
    override suspend fun getTransactionHistory(transactionFilterParams: TransactionFilterParams?): List<Transaction> {
        return safeApiCall<PagedTransactionResponseDto> {
            networkClient.get("$TRANSACTION_PATH?${transactionFilterParams?.toParameters()}")
        }.transactions.orEmpty().map { it.toEntity() }

    }

    override suspend fun getAllTransaction(): List<Transaction> {
        TODO("Not yet implemented")
    }

    override suspend fun getTransactionById(transactionId: Uuid): Transaction {
        return safeApiCall<TransactionDto> {
            networkClient.get("$TRANSACTION_PATH/$transactionId")
        }.toEntity()
    }

    private companion object {
        const val TRANSACTION_PATH = "wallet/transactions"
    }

}