package net.thechance.mena.wallet.data.repository.payment

import net.thechance.mena.wallet.data.exceptions.safeApiCall
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.domain.repository.PaymentRepository
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Single
class PaymentRepositoryImpl(private val networkClient: NetworkClient) : PaymentRepository {
    override suspend fun submitTransaction(transactionId: Uuid) {
        safeApiCall<Unit> {
            networkClient.post("$SUBMIT_PAYMENT_PATH/$transactionId") {
            }
        }
    }

    private companion object {
        const val PAYMENT_PATH = "/wallet/payment"
        const val SUBMIT_PAYMENT_PATH = "$PAYMENT_PATH/submit"
    }
}