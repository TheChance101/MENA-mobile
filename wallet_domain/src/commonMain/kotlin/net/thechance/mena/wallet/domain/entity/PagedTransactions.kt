package net.thechance.mena.wallet.domain.entity

data class PagedTransactions(
    val transactions: List<Transaction>,
    val currentPage: Long,
    val totalPages: Long
)