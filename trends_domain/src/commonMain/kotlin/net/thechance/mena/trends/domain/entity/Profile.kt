package net.thechance.mena.trends.domain.entity

data class Profile(
    val username: String,
    val firstName: String,
    val lastName: String,
    val profileImageUrl: String?,
)