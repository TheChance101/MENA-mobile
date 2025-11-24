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
import net.thechance.mena.admin_panel.domain.exceptions.NoInternetException
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

    private val testCountry = Country(
        name = "Iraq",
        callingCode = "+964",
        countryCodeName = "IQ",
        flagEmoji = "🇮🇶",
        phoneNumberRegex = "^\\d{10}$"
    )

    private val countryUi = DepositScreenState.CountryUiState(
        name = "Iraq",
        callingCode = "+964",
        countryCodeName = "IQ",
        flagEmoji = "🇮🇶"
    )

    @BeforeTest
    fun setup() {
        depositRepository = mock(mode = MockMode.autofill)
        useCase = DepositMoneyUseCase(depositRepository)

        everySuspend {
            depositRepository.getCountries()
        } returns listOf(testCountry)

        viewModel = DepositViewModel(
            depositMoneyUseCase = useCase,
            stringProvider = stringProvider,
            depositMoneyRepository = depositRepository,
            dispatcher = dispatcher
        )
    }

    @Test
    fun `deposit success updates state correctly`() = runTest(dispatcher) {
        everySuspend {
            depositRepository.depositMoney("+9647800000002", 100.0)
        } returns Unit

        viewModel.onPhoneNumberChanged("7800000002")
        viewModel.onAmountChanged("100")
        advanceUntilIdle()

        viewModel.onFillTheWalletButtonClicked()
        advanceUntilIdle()

        viewModel.state.test {
            val final = awaitItem()

            assertEquals(false, final.isDepositProcessLoading)
            assertEquals("", final.phoneNumber)
            assertEquals("", final.amount)
            assertEquals(true, final.snackBar.isSuccess)
            cancelAndIgnoreRemainingEvents()
        }
    }



    @Test
    fun `changing phone amount and country updates state`() = runTest(dispatcher) {
        viewModel.onPhoneNumberChanged("7800000002")
        advanceUntilIdle()

        viewModel.onAmountChanged("150")
        advanceUntilIdle()

        viewModel.onCountryCodeChanged(countryUi)
        advanceUntilIdle()

        viewModel.state.test {
            val s = awaitItem()
            assertEquals("7800000002", s.phoneNumber)
            assertEquals("150", s.amount)
            assertEquals(countryUi, s.selectedCountry)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `phone number filters non-digit characters`() = runTest(dispatcher) {
        viewModel.onPhoneNumberChanged("78abc00xyz000002")
        advanceUntilIdle()

        viewModel.state.test {
            val s = awaitItem()
            assertEquals("7800000002", s.phoneNumber)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loading countries success updates state`() = runTest(dispatcher) {
        advanceUntilIdle()

        viewModel.state.test {
            val final = awaitItem()

            assertEquals(false, final.isCountriesLoading)
            assertEquals(1, final.availableCountries.size)
            assertEquals("Iraq", final.availableCountries.first().name)
            assertEquals("+964", final.selectedCountry.callingCode)
            cancelAndIgnoreRemainingEvents()
        }
    }

}