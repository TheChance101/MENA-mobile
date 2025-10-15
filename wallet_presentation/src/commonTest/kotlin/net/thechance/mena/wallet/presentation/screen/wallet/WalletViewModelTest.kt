package net.thechance.mena.wallet.presentation.screen.wallet

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
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
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.base.UiState
import net.thechance.mena.wallet.presentation.model.SnackBarState
import net.thechance.mena.wallet.presentation.screen.helper.FakeStringProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class WalletViewModelTest {
    private val stringProvider = FakeStringProvider()
    private val balanceRepository = mock<BalanceRepository>(mode = MockMode.autofill)
    private val transactionRepository = mock<TransactionRepository>(mode = MockMode.autofill)
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
        val viewModel = WalletViewModel( stringProvider,balanceRepository, transactionRepository, testDispatcher)

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

        val viewModel = WalletViewModel( stringProvider,balanceRepository, transactionRepository, testDispatcher)
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

        val viewModel = WalletViewModel( stringProvider,balanceRepository, transactionRepository, testDispatcher)

        viewModel.state.test {
            skipItems(2)

            val errorState = awaitItem()
            assertEquals(UiState.Error(ErrorState.Unknown), errorState.balance)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getBalance should show snackbar with error when repository throws exception`() = runTest {
        val expectedException = RuntimeException("test error")
        everySuspend { balanceRepository.getBalance() } throws expectedException

        val viewModel = WalletViewModel( stringProvider,balanceRepository, transactionRepository, testDispatcher)

        viewModel.state.test {
            skipItems(3)

            val snackBarState = awaitItem()
            assertSnackBarState(isVisible = true, isSuccess = false, snackBarState = snackBarState.snackBar)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should send navigate back effect when onBackClicked is called`() = runTest {
        val viewModel = WalletViewModel( stringProvider,balanceRepository, transactionRepository, testDispatcher)
        viewModel.onBackClicked()

        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertTrue(effect is WalletEffect.NavigateBack)
        }
    }

    @Test
    fun `should send NavigateToTransactionHistory effect when onNavigateToTransactionHistoryClicked is called`() = runTest {
        val viewModel = WalletViewModel( stringProvider,balanceRepository, transactionRepository, testDispatcher)
        viewModel.onTransactionHistoryClicked()

        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertTrue(effect is WalletEffect.NavigateToTransactionHistory)
        }
    }

    @Test
    fun `should call getBalance when onRetryLoadBalanceClicked is called`() = runTest {
        val viewModel = WalletViewModel( stringProvider,balanceRepository, transactionRepository, testDispatcher)
        viewModel.onRetryLoadBalanceClicked()
        advanceUntilIdle()

        verifySuspend(exactly(2)) { balanceRepository.getBalance() }
    }

    @Test
    fun `onPaymentClicked should navigate to confirm payment screen when repository return pending transaction id`() =
        runTest {
            val viewModel = WalletViewModel( stringProvider,balanceRepository, transactionRepository, testDispatcher)

            val transactionId1 = Uuid.random()
            val amount1 = 200.0
            val receiverId = Uuid.random()
            everySuspend {
                transactionRepository.addPendingTransaction(
                    any(),
                    any(),
                    any()
                )
            } returns transactionId1

            viewModel.onPaymentClicked(amount1, receiverId)

            viewModel.uiEffect.test {
                val effect = awaitItem()
                assertTrue(effect is WalletEffect.NavigateToConfirmPaymentScreen)
            }
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