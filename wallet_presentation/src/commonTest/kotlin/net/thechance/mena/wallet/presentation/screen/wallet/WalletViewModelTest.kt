package net.thechance.mena.wallet.presentation.screen.wallet

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.wallet.domain.repository.BalanceRepository
import net.thechance.mena.wallet.presentation.base.SnackBarState
import net.thechance.mena.wallet.presentation.base.UiState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class WalletViewModelTest {
    private val balanceRepository = mock<BalanceRepository>(mode = MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getBalance should set balance with loading when initially called`() = runTest {
        val viewModel = WalletViewModel(balanceRepository, testDispatcher)

        viewModel.state.test {
            skipItems(1)

            val loadingState = awaitItem()
            assertTrue(loadingState.balance is UiState.Loading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getBalance should update balance with success when repository returns value`() = runTest {
        val expectedBalance = 250.0
        everySuspend { balanceRepository.getBalance() } returns expectedBalance

        val viewModel = WalletViewModel(balanceRepository, testDispatcher)
        advanceUntilIdle()

        viewModel.state.test {
            val successState = awaitItem()
            assertEquals(UiState.Success(expectedBalance), successState.balance)
        }
    }

    @Test
    fun `getBalance should update balance with error when repository throws exception`() = runTest {
        val expectedException = RuntimeException("test error")
        everySuspend { balanceRepository.getBalance() } throws expectedException

        val viewModel = WalletViewModel(balanceRepository, testDispatcher)

        viewModel.state.test {
            skipItems(2)

            val errorState = awaitItem()
            assertEquals(UiState.Error(expectedException), errorState.balance)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getBalance should show snackbar with error when repository throws exception`() = runTest {
        val expectedException = RuntimeException("test error")
        everySuspend { balanceRepository.getBalance() } throws expectedException

        val viewModel = WalletViewModel(balanceRepository, testDispatcher)

        viewModel.state.test {
            skipItems(3)

            val snackBarState = awaitItem()
            assertSnackBarState(isVisible = true, isSuccess = false, snackBarState = snackBarState.snackBar)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should send navigate back effect when onBackClicked is called`() = runTest {
        val viewModel = WalletViewModel(balanceRepository, testDispatcher)
        viewModel.onBackClicked()

        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertTrue(effect is WalletEffect.NavigateBack)
        }
    }

    @Test
    fun `should send NavigateToTransactionHistory effect when onNavigateToTransactionHistoryClicked is called`() = runTest {
        val viewModel = WalletViewModel(balanceRepository, testDispatcher)
        viewModel.onTransactionHistoryClicked()

        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertTrue(effect is WalletEffect.NavigateToTransactionHistory)
        }
    }

    @Test
    fun `should call getBalance when onRetryLoadBalanceClicked is called`() = runTest {
        val viewModel = WalletViewModel(balanceRepository, testDispatcher)
        viewModel.onRetryLoadBalanceClicked()
        advanceUntilIdle()

        verifySuspend(exactly(2)) { balanceRepository.getBalance() }
    }

    private fun assertSnackBarState(
        isVisible: Boolean,
        isSuccess: Boolean,
        snackBarState: SnackBarState
    ) {
        assertEquals(isVisible, snackBarState.isVisible)
        assertEquals(isSuccess, snackBarState.isSuccess)
    }
}