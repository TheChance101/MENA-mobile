package net.thechance.mena.admin_panel.domain.use_case.deposit

import net.thechance.mena.admin_panel.domain.exceptions.InvalidAmountException
import net.thechance.mena.admin_panel.domain.exceptions.InvalidPhoneNumberException
import net.thechance.mena.admin_panel.domain.model.Country
import net.thechance.mena.admin_panel.domain.repository.depositMoney.DepositMoneyRepository
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single

@Single
class DepositMoneyUseCase(
    @Provided
    private val depositRepository: DepositMoneyRepository,
) {
    suspend fun deposit(phoneNumber: String, amount: Double, selectedCountry: Country) {
        validatePhoneNumber(phoneNumber, selectedCountry.phoneNumberRegex)
        validateAmount(amount)
        depositRepository.depositMoney(selectedCountry.callingCode + phoneNumber, amount)

    }

    private fun validatePhoneNumber(phoneNumber: String, phoneRegex: String) {
        if (!phoneNumber.matches(Regex(phoneRegex))) throw InvalidPhoneNumberException()
    }

    private fun validateAmount(amount: Double) {
        if (amount <= 0) throw InvalidAmountException()
    }
}