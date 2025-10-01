package net.thechance.mena.wallet.domain.model

import kotlinx.datetime.LocalDate

data class TransactionFilterParams(
    val types: List<TransactionType>?,
    val status: TransactionStatus?,
    val startDate: LocalDate?,
    val endDate :LocalDate?,
)
