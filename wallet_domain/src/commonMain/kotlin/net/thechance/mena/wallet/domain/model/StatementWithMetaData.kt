package net.thechance.mena.wallet.domain.model

import net.thechance.mena.wallet.domain.entity.Statement

data class StatementWithMetaData(
    val byteArray: ByteArray,
    val startDate: String,
    val endDate: String,
    val totalInflows: Double,
    val totalOutflows: Double
)