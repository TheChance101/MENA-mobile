package net.thechance.mena.admin_panel.data.remote.dto.dukan

import kotlinx.serialization.Serializable

@Serializable
data class UpdateDukanStatusRequestDto(
    val status: String,
    val message: String
)