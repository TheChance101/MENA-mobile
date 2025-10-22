package net.thechance.mena.dukan.domain.model

import net.thechance.mena.dukan.domain.entity.Dukan

data class MyDukanStatus(
    val status: Dukan.Status,
    val dukanName: String
)