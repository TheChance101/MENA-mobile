package net.thechance.mena.wallet.domain.model

import kotlinx.datetime.LocalDate

data class StatementWithMetaData(
    val byteArray: ByteArray,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalInflows: Double,
    val totalOutflows: Double
)