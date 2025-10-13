package net.thechance.mena.wallet.domain.entity

import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
data class User(
    val name: String,
    val imgUrl: String?
)