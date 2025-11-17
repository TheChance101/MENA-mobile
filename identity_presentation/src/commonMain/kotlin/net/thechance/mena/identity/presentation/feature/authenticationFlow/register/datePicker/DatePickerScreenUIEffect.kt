package net.thechance.mena.identity.presentation.feature.authenticationFlow.register.datePicker

import net.thechance.mena.identity.presentation.feature.authenticationFlow.register.shared.uiState.RegisterUIState

sealed interface DatePickerScreenUIEffect {
    data class NavigateToSelectGender(val registerUIState: RegisterUIState) :
        DatePickerScreenUIEffect
}