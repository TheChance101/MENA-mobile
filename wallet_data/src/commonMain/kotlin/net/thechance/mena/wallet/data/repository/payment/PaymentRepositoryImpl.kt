package net.thechance.mena.wallet.data.repository.payment

import io.ktor.client.request.setBody
import net.thechance.mena.wallet.data.dto.PaymentConfirmationDto
import net.thechance.mena.wallet.data.dto.PaymentConfirmationRequest
import net.thechance.mena.wallet.data.exceptions.safeApiCall
import net.thechance.mena.wallet.data.mapper.toEntity
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.domain.model.PaymentConfirmation
import net.thechance.mena.wallet.domain.repository.PaymentRepository
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Single
class PaymentRepositoryImpl(private val networkClient: NetworkClient) : PaymentRepository {
    override suspend fun getPaymentConfirmation(
        receiverId: Uuid,
        amount: Double
    ): PaymentConfirmation {
        val requestBody = PaymentConfirmationRequest(receiverId.toString(), amount)
        return safeApiCall<PaymentConfirmationDto> {
            networkClient.post(CONFIRM_PAYMENT_PATH) {
                setBody(requestBody)
            }
        }.toEntity()
    }

    private companion object {
        const val PAYMENT_PATH = "/wallet/payment"
        const val CONFIRM_PAYMENT_PATH = "$PAYMENT_PATH/isValid"
    }
}