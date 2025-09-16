package net.thechance.mena.faith.domain.entity

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class Bookmark(
    val id: Int,
    val surah: Surah,
    val ayah: Ayah,
    val createdAt: Instant,
)