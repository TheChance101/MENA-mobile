package net.thechance.mena.wallet.data.dto.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Entity(tableName = "statement")
data class LocalStatement @OptIn(ExperimentalTime::class) constructor(
    @PrimaryKey val id: String,
    val startDate: String,
    val endDate: String,
    val totalInflows: Double,
    val totalOutflows: Double,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val fileName: String,
    val userId :String
)