package net.thechance.mena.wallet.data.database

data class StatementWithMetaDataDto(
    val byteArray: ByteArray,
    val startDate: String,
    val endDate: String,
    val totalInflows: Double,
    val totalOutflows: Double,
)