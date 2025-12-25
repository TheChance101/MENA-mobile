package net.thechance.mena.identity.presentation.screen.register.shared

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.thechance.mena.identity.domain.model.AuthenticationTokens

@Serializable
data class AuthUIState(
    val authTokens: AuthenticationTokensUiState? = null,
    val phoneNumber: PhoneNumberUIState? = null
){
    @Serializable
    data class AuthenticationTokensUiState(
        val accessToken: String,
        val refreshToken: String
    )
}

fun AuthUIState.toAuthUIStateJsonString(): String{
    return Json.encodeToString(this)
}

fun AuthenticationTokens.toAuthUIState(): AuthUIState.AuthenticationTokensUiState {
    return AuthUIState.AuthenticationTokensUiState(
            accessToken = accessToken,
            refreshToken = refreshToken
    )
}

fun convertJsonStringToAuthUIState(jsonString: String): AuthUIState {
    return Json.decodeFromString(jsonString)
}

fun AuthUIState.AuthenticationTokensUiState.toAuthenticationTokens(): AuthenticationTokens {
    return AuthenticationTokens(
        accessToken = accessToken,
        refreshToken = refreshToken
    )
}