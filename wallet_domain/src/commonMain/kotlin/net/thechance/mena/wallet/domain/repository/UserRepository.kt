package net.thechance.mena.wallet.domain.repository

import net.thechance.mena.wallet.domain.entity.User
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface UserRepository {
    suspend fun getReceiverByTransactionId(id: Uuid): User
}