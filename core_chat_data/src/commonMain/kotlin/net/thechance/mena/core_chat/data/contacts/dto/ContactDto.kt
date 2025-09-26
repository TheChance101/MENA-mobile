package net.thechance.mena.core_chat.data.contacts.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContactDto (
    @SerialName("firstName")
    val firstName: String? = null,
    @SerialName("lastName")
    val lastName: String? = null,
    @SerialName("phoneNumber")
    val phoneNumber: String? = null,
    @SerialName("isMenaUser")
    val isMenaUser: Boolean? = null,
    @SerialName("imageUrl")
    val imageUrl: String? = null,
)