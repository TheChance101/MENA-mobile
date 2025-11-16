package net.thechance.mena.admin_panel.domin.use_case

import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import net.thechance.mena.admin_panel.domain.exceptions.InvalidAmountException
import net.thechance.mena.admin_panel.domain.exceptions.InvalidPhoneNumberException
import net.thechance.mena.admin_panel.domain.model.Country
import net.thechance.mena.admin_panel.domain.repository.depositMoney.DepositMoneyRepository
import net.thechance.mena.admin_panel.domain.use_case.deposit.DepositMoneyUseCase
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class DepositMoneyUseCaseTest {

    private lateinit var depositRepository: DepositMoneyRepository
    private lateinit var useCase: DepositMoneyUseCase

    @BeforeTest
    fun setup() {
        depositRepository = mock(mode = MockMode.autofill)
        useCase = DepositMoneyUseCase(depositRepository)
    }

    @Test
    fun `deposit should throw InvalidPhoneNumberException for invalid phone`() = runTest {
        val invalidPhone = "1234"

        assertFailsWith<InvalidPhoneNumberException> {
            useCase.deposit(invalidPhone, 100.0, country)
        }
    }

    @Test
    fun `deposit should throw InvalidAmountException for zero or negative amount`() = runTest {
        assertFailsWith<InvalidAmountException> {
            useCase.deposit("01234567890", 0.0, country)
        }
    }

    @Test
    fun `deposit should call repository when data is valid`() = runTest {
        val phone = "01234567890"
        val transformedPhone = "+20$phone"
        val amount = 150.0

        everySuspend {
            depositRepository.depositMoney(transformedPhone, amount)
        } returns Unit

        useCase.deposit(phone, amount, country)

        verifySuspend { depositRepository.depositMoney(transformedPhone, amount) }
    }
    private val country = Country(
        name = "Test Country",
        callingCode = "+20",
        phoneNumberRegex = "^\\d{11}$",
        countryCodeName ="Eg",
        flagEmoji = "",
    )

}
