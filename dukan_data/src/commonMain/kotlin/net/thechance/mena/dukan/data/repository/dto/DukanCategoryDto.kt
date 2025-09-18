package net.thechance.mena.dukan.data.repository.dto

import kotlinx.serialization.SerialName

data class DukanCategoryDto(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String,
    @SerialName("icon")
    val icon: String,
)