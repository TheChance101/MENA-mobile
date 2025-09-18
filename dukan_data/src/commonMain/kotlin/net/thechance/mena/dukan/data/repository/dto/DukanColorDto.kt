package net.thechance.mena.dukan.data.repository.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DukanColorDto (
    @SerialName("id")
    val id : String,
    @SerialName("hexCode")
    val hexCode : String,
)