package net.thechance.mena.admin_panel.presentation.screen.dukan_details

import net.thechance.mena.admin_panel.domain.entity.dukan.Dukan
import net.thechance.mena.admin_panel.domain.entity.dukan.Dukan.ActivationStatus
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Dukan.toUiState() = DukanDetailsScreenState.DukanItemUiState(
    id = id,
    name = name,
    address = address,
    imageUrl = imageUrl,
    categories = categories.map { it.title },
    latitude = latitude,
    longitude = longitude,
    dukanStatus = when (activationStatus) {
        ActivationStatus.ACTIVATED -> DukanDetailsScreenState.DukanStatus.ACTIVE
        ActivationStatus.DEACTIVATED -> DukanDetailsScreenState.DukanStatus.DEACTIVE
        else -> DukanDetailsScreenState.DukanStatus.DEACTIVE
    }
)