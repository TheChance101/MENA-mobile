@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.domain.entity

import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class Statement(
    val id: Uuid,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalInflows: Double,
    val totalOutflows: Double,
    val pathUrl: String,
    val fileName: String
)
