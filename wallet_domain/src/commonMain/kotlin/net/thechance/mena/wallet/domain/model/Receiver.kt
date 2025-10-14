package net.thechance.mena.wallet.domain.model

import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
data class Receiver(
    val name: String,
    val imgUrl: String?
)