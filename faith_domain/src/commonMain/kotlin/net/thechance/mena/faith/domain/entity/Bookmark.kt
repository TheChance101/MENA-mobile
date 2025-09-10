package net.thechance.mena.faith.domain.entity

data class Bookmark(
    val id: Int,
    val surah: Surah,
    val ayah: Ayah,
    val createdAt: Long // LocalDateTime
)
