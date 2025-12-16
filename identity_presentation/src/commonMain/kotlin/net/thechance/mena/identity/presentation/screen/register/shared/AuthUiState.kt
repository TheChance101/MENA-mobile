package net.thechance.mena.identity.presentation.screen.register.shared

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.thechance.mena.identity.domain.model.AuthenticationTokens

@Serializable
data class AuthUiState(
    val authTokens: AuthenticationTokensUiState? = null,
    val phoneNumber: PhoneNumberUIState? = null
){
    @Serializable
    data class AuthenticationTokensUiState(
        val accessToken: String,
        val refreshToken: String
    )
}

fun AuthUiState.toAuthUIStateJsonString(): String{
    return Json.encodeToString(this)
}

fun AuthenticationTokens.toAuthUIState(): AuthUiState.AuthenticationTokensUiState {
    return AuthUiState.AuthenticationTokensUiState(
            accessToken = accessToken,
            refreshToken = refreshToken
    )
}

fun convertJsonStringToAuthUIState(jsonString: String): AuthUiState {
    return Json.decodeFromString(jsonString)
}

fun AuthUiState.AuthenticationTokensUiState.toAuthenticationTokens(): AuthenticationTokens {
    return AuthenticationTokens(
        accessToken = accessToken,
        refreshToken = refreshToken
    )
}