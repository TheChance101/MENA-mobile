package net.thechance.mena.wallet.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "statement")
data class Statement(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val startDate: String,
    val endDate: String,
    val totalInflows: Double,
    val totalOutflows: Double,
    val fileName: String,
    val createdAt: Long ,
)