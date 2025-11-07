package net.thechance.mena.identity.presentation.screen.register.selectGender

import net.thechance.mena.identity.domain.entity.Gender

import org.jetbrains.compose.resources.StringResource

data class SelectGenderScreenUIState(
    val isRegisterEnabled: Boolean = false,
    val isRegisterLoading: Boolean = false,
    val gender: Gender? = null,
    val errorMessage: StringResource? = null
)