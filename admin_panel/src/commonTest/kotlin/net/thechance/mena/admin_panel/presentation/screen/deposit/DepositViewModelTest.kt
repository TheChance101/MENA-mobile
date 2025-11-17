package net.thechance.mena.admin_panel.presentation.screen.deposit

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.admin_panel.domain.exceptions.InvalidPhoneNumberException
import net.thechance.mena.admin_panel.domain.exceptions.NoInternetException
import net.thechance.mena.admin_panel.domain.model.Country
import net.thechance.mena.admin_panel.domain.repository.depositMoney.DepositMoneyRepository
import net.thechance.mena.admin_panel.domain.use_case.deposit.DepositMoneyUseCase
import net.thechance.mena.admin_panel.presentation.utils.StringProvider
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.success_deposit_title
import net.thechance.mena.admin_panel.resources.success_deposit_description
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class DepositViewModelTest {

    private val useCase = mock<DepositMoneyUseCase>(mode = MockMode.autofill)
    private val stringProvider = mock<StringProvider>(mode = MockMode.autofill)
    private val repository = mock<DepositMoneyRepository>(mode = MockMode.autofill)

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: DepositViewModel

    private val fakeCountries = listOf(
        Country(
            name = "Iraq",
            callingCode = "+964",
            countryCodeName = "IQ",
            flagEmoji = "",
            phoneNumberRegex = "",
        )
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        everySuspend { repository.getCountries() } returns fakeCountries

        everySuspend {
            useCase.deposit(any(), any(), any())
        } returns Unit

        everySuspend {
            stringProvider.getString(Res.string.success_deposit_title)
        } returns "Success"

        everySuspend {
            stringProvider.getString(Res.string.success_deposit_description)
        } returns "Deposit Completed Successfully"
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun TestScope.initViewModel() {
        viewModel = DepositViewModel(
            depositMoneyUseCase = useCase,
            stringProvider = stringProvider,
            depositMoneyRepository = repository,
            dispatcher = testDispatcher
        )
        advanceUntilIdle()
    }

    @Test
    fun `should load countries on initialization`() = runTest(testDispatcher) {
        initViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isCountriesLoading)
            assertEquals(1, state.availableCountries.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should update phone number and filter non digits`() = runTest(testDispatcher) {
        initViewModel()

        viewModel.onPhoneNumberChanged("+964 7800000002ABC")
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals("9647800000002", state.phoneNumber)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should update amount and filter non digits`() = runTest(testDispatcher) {
        initViewModel()

        viewModel.onAmountChanged("12ab34")
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals("1234", state.amount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should deposit successfully`() = runTest(testDispatcher) {
        initViewModel()

        viewModel.onPhoneNumberChanged("9647800000002")
        viewModel.onAmountChanged("25000")
        advanceUntilIdle()

        viewModel.onFillTheWalletButtonClicked()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isDepositProcessLoading)
            assertEquals("", state.amount)
            assertEquals("", state.phoneNumber)
            assertTrue(state.snackBar.isSuccess)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should show NoInternet error`() = runTest(testDispatcher) {
        everySuspend { useCase.deposit(any(), any(), any()) } throws NoInternetException()

        initViewModel()

        viewModel.onPhoneNumberChanged("9647800000002")
        viewModel.onAmountChanged("5000")
        advanceUntilIdle()

        viewModel.onFillTheWalletButtonClicked()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.snackBar.isSuccess)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should show phone number error`() = runTest(testDispatcher) {
        everySuspend { useCase.deposit(any(), any(), any()) } throws InvalidPhoneNumberException()

        initViewModel()

        viewModel.onPhoneNumberChanged("123")
        viewModel.onAmountChanged("5000")

        viewModel.onFillTheWalletButtonClicked()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.snackBar.isSuccess)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
