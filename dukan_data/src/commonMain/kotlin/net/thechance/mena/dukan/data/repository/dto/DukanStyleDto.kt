package net.thechance.mena.dukan.data.repository.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DukanStyleDto(
    @SerialName("styles")
    val styles: List<String>
)