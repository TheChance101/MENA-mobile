package net.thechance.mena.wallet.data.repository.user

import io.ktor.client.request.parameter
import net.thechance.mena.wallet.data.dto.UserDto
import net.thechance.mena.wallet.data.exceptions.safeApiCall
import net.thechance.mena.wallet.data.mapper.toEntity
import net.thechance.mena.wallet.data.network_client.NetworkClient
import net.thechance.mena.wallet.domain.entity.User
import net.thechance.mena.wallet.domain.repository.UserRepository
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Single
class UserRepositoryImpl (
    private val networkClient: NetworkClient
) : UserRepository{
    override suspend fun getReceiverByTransactionId(id: Uuid): User {
        return safeApiCall<UserDto> {
            networkClient.get("$TRANSACTION_PATH$RECEIVER_DETAILS"){
                parameter(TRANSACTION_ID_PARAM, id)
            }
        }.toEntity(id)
    }

    private companion object {
        const val TRANSACTION_PATH = "wallet/transactions"
        const val RECEIVER_DETAILS = "/receiver-details"
        const val TRANSACTION_ID_PARAM = "transactionId"
    }
}