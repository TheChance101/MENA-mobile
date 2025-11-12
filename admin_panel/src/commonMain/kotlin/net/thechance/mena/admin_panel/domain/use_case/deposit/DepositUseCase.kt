package net.thechance.mena.admin_panel.domain.use_case.deposit

import net.thechance.mena.admin_panel.domain.exceptions.InvalidAmountException
import net.thechance.mena.admin_panel.domain.exceptions.InvalidPhoneNumberException
import net.thechance.mena.admin_panel.domain.model.Country
import net.thechance.mena.admin_panel.domain.repository.deposit.DepositRepository
import org.koin.core.annotation.Single

@Single
class DepositUseCase(
    private val depositRepository: DepositRepository,
) {
    suspend fun deposit(phoneNumber: String, amount: Double, selectedCountry: Country) {
        validatePhoneNumber(phoneNumber, selectedCountry.phoneNumberRegex)
        validateAmount(amount)
        depositRepository.deposit(selectedCountry.callingCode + phoneNumber, amount)

    }

    suspend fun getCountries(langauge: String): List<Country> {
        return depositRepository.getCountries(langauge)
    }

    private fun validatePhoneNumber(phoneNumber: String, phoneRegex: String) {
        if (!phoneNumber.matches(Regex(phoneRegex))) throw InvalidPhoneNumberException()
    }

    private fun validateAmount(amount: Double) {
        if (amount <= 0) throw InvalidAmountException()
    }


}