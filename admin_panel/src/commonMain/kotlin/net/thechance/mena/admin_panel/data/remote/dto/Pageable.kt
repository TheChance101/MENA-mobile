package net.thechance.mena.admin_panel.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.thechance.mena.admin_panel.data.remote.dto.Sort

@Serializable
data class Pageable(
    @SerialName("pageNumber")
    val pageNumber: Int? = 0,
    @SerialName("pageSize")
    val pageSize: Int? = 0,
    @SerialName("sort")
    val sort: Sort? = Sort(),
    @SerialName("offset")
    val offset: Int? = 0,
    @SerialName("paged")
    val paged: Boolean? = false,
    @SerialName("unpaged")
    val unpaged: Boolean? = false
)