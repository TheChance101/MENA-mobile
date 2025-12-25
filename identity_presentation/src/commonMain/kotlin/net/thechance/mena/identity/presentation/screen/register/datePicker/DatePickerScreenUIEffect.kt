package net.thechance.mena.identity.presentation.screen.register.datePicker

import net.thechance.mena.identity.presentation.screen.register.shared.RegisterUIState

sealed interface DatePickerScreenUIEffect {
    data class NavigateToSelectGender(val registerUIState: RegisterUIState) : DatePickerScreenUIEffect
}