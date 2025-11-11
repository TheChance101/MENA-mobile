package net.thechance.mena.admin_panel.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sort(
    @SerialName("empty")
    val empty: Boolean? = null,
    @SerialName("sorted")
    val sorted: Boolean? = null,
    @SerialName("unsorted")
    val unsorted: Boolean? = null
)