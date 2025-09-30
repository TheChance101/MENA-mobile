package net.thechance.mena.wallet.data.mapper

import net.thechance.mena.wallet.data.dto.PagedTransactionResponseDto
import net.thechance.mena.wallet.domain.entity.PagedTransactions

fun PagedTransactionResponseDto.toEntity(): PagedTransactions {
    return PagedTransactions(
        transactions = this.transactions.orEmpty().map { it.toEntity() },
        currentPage = this.page ?: 0L,
        totalPages = this.totalPages ?: 0L
    )
}