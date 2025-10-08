package net.thechance.mena.wallet.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity(tableName = "statement")
data class Statement @OptIn(ExperimentalUuidApi::class) constructor(
    @PrimaryKey val id: Uuid,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalInflows: Double,
    val totalOutflows: Double,
    val pathUrl: String,
    val fileName: String
)