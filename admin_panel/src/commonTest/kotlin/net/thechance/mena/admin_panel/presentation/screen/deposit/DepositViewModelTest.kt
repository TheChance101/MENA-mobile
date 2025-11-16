package net.thechance.mena.admin_panel.presentation.screen.deposit

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.thechance.mena.admin_panel.domain.exceptions.InvalidPhoneNumberException
import net.thechance.mena.admin_panel.domain.model.Country
import net.thechance.mena.admin_panel.domain.repository.depositMoney.DepositMoneyRepository
import net.thechance.mena.admin_panel.domain.use_case.deposit.DepositMoneyUseCase
import net.thechance.mena.admin_panel.presentation.utils.StringProvider
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DepositViewModelTest {

    private lateinit var depositRepository: DepositMoneyRepository
    private lateinit var useCase: DepositMoneyUseCase
    private lateinit var viewModel: DepositViewModel

    private val dispatcher = StandardTestDispatcher()

    private val stringProvider: StringProvider = mockk(relaxed = true)
    private val countryUi = DepositScreenState.CountryUiState(
        name = "Test Country",
        callingCode = "+20",
        phoneNumberRegex = "^\\d{11}$",
        countryCodeName ="Eg",
        flagEmoji = "",
    )

    @BeforeTest
    fun setup() {
        depositRepository = mock(mode = MockMode.autofill)
        useCase = DepositMoneyUseCase(depositRepository)

        everySuspend { depositRepository.getCountries() } returns listOf(
            Country( "Test Country", "+20", "eg","","^\\d{11}$")
        )

        viewModel = DepositViewModel(
            depositMoneyUseCase = useCase,
            stringProvider = stringProvider,
            depositMoneyRepository = depositRepository,
            dispatcher = dispatcher
        )
    }

    @Test
    fun `deposit success updates state correctly`() = runTest {
        everySuspend {
            depositRepository.depositMoney("+201234567890", 100.0)
        } returns Unit

        viewModel.onPhoneNumberChanged("01234567890")
        viewModel.onAmountChanged("100")

        viewModel.state.test {
            viewModel.onFillTheWalletButtonClicked()
            advanceUntilIdle()

            val final = awaitItem()

            assertEquals(false, final.isDepositProcessLoading)
            assertEquals("", final.phoneNumber)
            assertEquals("", final.amount)
        }
    }

    @Test
    fun `deposit error shows error snackbar and stops loading`() = runTest {
        everySuspend {
            depositRepository.depositMoney(any(), any())
        } throws InvalidPhoneNumberException()

        viewModel.onPhoneNumberChanged("123")
        viewModel.onAmountChanged("100")

        viewModel.state.test {
            viewModel.onFillTheWalletButtonClicked()
            advanceUntilIdle()

            val final = awaitItem()

            assertEquals(false, final.isDepositProcessLoading)
            assertEquals(false, final.snackBar.isSuccess)
            assertEquals(true, final.snackBar.isVisible)
        }
    }

    @Test
    fun `changing phone amount and country updates state`() = runTest {
        viewModel.state.test {
            viewModel.onPhoneNumberChanged("0123")
            viewModel.onAmountChanged("150")
            viewModel.onCountryCodeChanged(countryUi)

            val s = awaitItem()
            assertEquals("0123", s.phoneNumber)
            assertEquals("150", s.amount)
            assertEquals(countryUi, s.selectedCountry)
        }
    }
}
