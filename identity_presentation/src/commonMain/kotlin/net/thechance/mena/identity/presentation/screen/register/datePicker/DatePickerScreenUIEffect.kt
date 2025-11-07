package net.thechance.mena.identity.presentation.screen.register.datePicker

import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.domain.entity.PhoneNumber

sealed interface DatePickerScreenUIEffect {
    data class NavigateToSelectGender(
        val phoneNumber: PhoneNumber,
        val firstName: String,
        val lastName: String,
        val username: String,
        val password: String,
        val birthDate: LocalDate
    ) : DatePickerScreenUIEffect
}