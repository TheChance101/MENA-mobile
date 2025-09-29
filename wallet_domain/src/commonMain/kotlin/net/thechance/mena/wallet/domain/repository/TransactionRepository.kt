package net.thechance.mena.wallet.domain.repository

import net.thechance.mena.wallet.domain.entity.Transaction

interface TransactionRepository {
    suspend fun getAll(): List<Transaction>
}