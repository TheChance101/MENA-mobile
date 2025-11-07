package net.thechance.mena.identity.presentation.screen.register.enterName

import org.jetbrains.compose.resources.StringResource

data class EnterNameUIState(
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val firstNameError: Int? = null,
    val lastNameError: Int? = null,
    val usernameError: Int? = null,
    val isNextEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val isCheckingUsername: Boolean = false,
    val errorMessage: StringResource? = null
){
    fun isValidInput(): Boolean {
        return firstName.isNotBlank() && lastName.isNotBlank() && username.isNotBlank()
    }
}