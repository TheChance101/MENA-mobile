package net.thechance.mena.dukan.data.repository.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyDukanStatusDto(
    @SerialName("statues")
    val status: String,
    @SerialName("name")
    val dukanName: String
)
