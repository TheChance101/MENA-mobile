package net.thechance.mena.trends.domain.entity

data class Category(
    val id: String,
    val name: String,
    val emoji: String,
    val isSelected: Boolean
)