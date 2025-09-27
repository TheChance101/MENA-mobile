package net.thechance.mena.wallet.data.repository.balance

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Single
class TransactionRepositoryImpl() : TransactionRepository {
    override suspend fun getTransactionDetails(transactionId: Uuid): Transaction {
        return Transaction(
            id = transactionId,
            createdAt = LocalDateTime(
                date = LocalDate(2025, 8, 20),
                time = LocalTime(12, 0)
            ),
            amount = 5000.0,
            status = Transaction.Status.SUCCESS,
            senderId = Uuid.random(),
            senderName = "Nour Elhoda",
            receiverId = Uuid.random(),
            receiverName = "Nour Elhoda",
            type = Transaction.Type.RECEIVED
        )
    }
}