package net.thechance.mena.identity.presentation.screen.register.datePicker

sealed interface DatePickerScreenUIEffect {
    data object NavigateToSelectGender : DatePickerScreenUIEffect
}