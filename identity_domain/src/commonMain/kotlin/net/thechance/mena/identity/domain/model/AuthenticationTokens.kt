package net.thechance.mena.identity.domain.model

data class AuthenticationTokens(
    val accessToken: String,
    val refreshToken: String
)