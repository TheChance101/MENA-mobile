package net.thechance.mena.admin_panel.presentation.screen.dukan_details

import net.thechance.mena.admin_panel.domain.entity.dukan.Dukan

fun Dukan.toUi() = DukanDetailsScreenState.DukanUi(
    name = name,
    address = address,
    imageUrl = imageUrl,
    categories = categories.map { it.title }
)