package net.thechance.mena.wallet.data.repository.balance

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Single
class TransactionRepositoryImpl(): TransactionRepository {
        @OptIn(ExperimentalUuidApi::class)
        override suspend fun getTransactionHistory(): List<Transaction> {

            return listOf(
                Transaction(
                    id = Uuid.random(),
                    createdAt =LocalDateTime(
                        date = LocalDate(2025, 8, 20),
                        time = LocalTime(12, 0)
                    ),
                    amount = 120.0,
                    status = Transaction.Status.SUCCESS,
                    senderId = Uuid.random(),
                    senderName = "Alice",
                    receiverId = Uuid.random(),
                    receiverName = "Bob",
                    type = Transaction.Type.SENT
                ),
                Transaction(
                    id = Uuid.random(),
                    createdAt = LocalDateTime(
                        date = LocalDate(2025, 8, 20),
                        time = LocalTime(12, 0)
                    ),
                    amount = 75.5,
                    status = Transaction.Status.FAIL,
                    senderId = Uuid.random(),
                    senderName = "Charlie",
                    receiverId = Uuid.random(),
                    receiverName = "You",
                    type = Transaction.Type.RECEIVED
                ),
                Transaction(
                    id = Uuid.random(),
                    createdAt = LocalDateTime(
                        date = LocalDate(2025, 8, 20),
                        time = LocalTime(12, 0)
                    ),
                    amount = 200.0,
                    status = Transaction.Status.SUCCESS,
                    senderId = Uuid.random(),
                    senderName = "Online Shop",
                    receiverId = Uuid.random(),
                    receiverName = "You",
                    type = Transaction.Type.ONLINE_PURCHASE
                )
            )
        }
    }
