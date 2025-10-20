package net.thechance.mena.wallet.data.repository.payment

import net.thechance.mena.wallet.data.utils.safeApiCall
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.domain.repository.PaymentRepository
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Single
class PaymentRepositoryImpl(private val networkClient: NetworkClient) : PaymentRepository {
    override suspend fun submitTransaction(transactionId: Uuid) = safeApiCall<Unit> {
        networkClient.post(getSubmitTransactionPath(transactionId))
    }

    private companion object {
        fun getSubmitTransactionPath(transactionId: Uuid) = "/wallet/payment/$transactionId/submit"
    }
}