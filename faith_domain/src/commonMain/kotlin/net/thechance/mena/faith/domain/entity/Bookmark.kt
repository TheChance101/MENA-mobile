package net.thechance.mena.faith.domain.entity

import kotlinx.datetime.LocalDateTime

data class Bookmark(
    val id: Int,
    val surah: Surah,
    val ayah: Ayah,
    val createdAt: LocalDateTime
)
