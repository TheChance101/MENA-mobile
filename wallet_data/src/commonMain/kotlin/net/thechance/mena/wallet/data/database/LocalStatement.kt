package net.thechance.mena.wallet.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Entity(tableName = "statement")
data class LocalStatement @OptIn(ExperimentalTime::class) constructor(
    @PrimaryKey(autoGenerate = true) val id: Long=0,
    val startDate: String,
    val endDate: String,
    val totalInflows: Double,
    val totalOutflows: Double,
    val createdAt: Long= Clock.System.now().toEpochMilliseconds(),
    val fileName:String
)