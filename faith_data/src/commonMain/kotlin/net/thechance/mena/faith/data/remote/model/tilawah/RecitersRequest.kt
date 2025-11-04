package net.thechance.mena.faith.data.remote.model.tilawah

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecitersRequest(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("arabicName")
    val arabicName: String,
    @SerialName("tilawahType")
    val tilawahType: String
)