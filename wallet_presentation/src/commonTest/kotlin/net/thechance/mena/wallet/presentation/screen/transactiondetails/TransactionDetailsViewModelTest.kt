@file:OptIn(ExperimentalUuidApi::class, ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.transactiondetails

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.base.UiState
import net.thechance.mena.wallet.presentation.utils.ImageSharer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionDetailsViewModelTest {
    private val transactionRepository = mock<TransactionRepository>(mode = MockMode.autofill)
    private val imageSharer = mock<ImageSharer>(mode = MockMode.autofill)
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
    fun `getTransactionDetails should set transaction with loading when initially called`() = runTest {
        everySuspend { transactionRepository.getTransactionDetails(any()) } returns transaction1

        val viewModel = TransactionDetailsViewModel(
            imageSharer = imageSharer,
            transactionRepository = transactionRepository,
            ioDispatcher = testDispatcher
        )

        viewModel.state.test {
            skipItems(1)
            val initialState = awaitItem()
            assertTrue(initialState.transactionDetailsUiState is UiState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getTransactionDetails should update transaction ui state with success when repository returns value`() = runTest {
        everySuspend { transactionRepository.getTransactionDetails(any()) } returns transaction1

        val viewModel = TransactionDetailsViewModel(
            imageSharer = imageSharer,
            transactionRepository = transactionRepository,
            ioDispatcher = testDispatcher
        )

        viewModel.state.test {
            skipItems(2)
            val successState = awaitItem()
            assertTrue(successState.transactionDetailsUiState is UiState.Success)
            val transactionUiState = (successState.transactionDetailsUiState as UiState.Success).data
            assertEquals(transaction1uiState, transactionUiState)
        }
    }

    @Test
    fun `getTransactionDetails should update transaction ui state with error when repository fails`() = runTest {
        val expectedError = Exception()
        everySuspend { transactionRepository.getTransactionDetails(any()) } throws expectedError

        val viewModel = TransactionDetailsViewModel(
            imageSharer = imageSharer,
            transactionRepository = transactionRepository,
            ioDispatcher = testDispatcher
        )

        viewModel.state.test {
            skipItems(2)
            val errorState = awaitItem()
            assertTrue(errorState.transactionDetailsUiState is UiState.Error)
            val error = errorState.transactionDetailsUiState.throwable
            assertEquals(expectedError, error)
        }
    }

    @Test
    fun `onBackButtonClicked should send NavigateBack effect`() = runTest {
        everySuspend { transactionRepository.getTransactionDetails(any()) } returns transaction1

        val viewModel = TransactionDetailsViewModel(
            imageSharer = imageSharer,
            transactionRepository = transactionRepository,
            ioDispatcher = testDispatcher
        )

        viewModel.uiEffect.test {
            viewModel.onBackButtonClicked()
            assertEquals(TransactionDetailsEffect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `onScreenShotCaptured should share image and reset loading state to false when success`() = runTest {
        everySuspend { transactionRepository.getTransactionDetails(any()) } returns transaction1
        everySuspend { imageSharer.shareImage(any(), any(), any()) } returns Unit

        val byteArray = byteArrayOf()
        val viewModel = TransactionDetailsViewModel(
            imageSharer = imageSharer,
            transactionRepository = transactionRepository,
            ioDispatcher = testDispatcher
        )

        viewModel.state.test {
            skipItems(2)

            viewModel.onScreenShotCaptured(byteArray, "test_file")

            val finalState = awaitItem()
            assertTrue(!finalState.isShareReceiptBtnLoading)
        }
    }

    @Test
    fun `onScreenShotCaptured should reset loading state to false when fail`() = runTest {
        everySuspend { transactionRepository.getTransactionDetails(any()) } returns transaction1
        everySuspend { imageSharer.shareImage(any(), any(), any()) } throws Exception()

        val byteArray = byteArrayOf()
        val viewModel = TransactionDetailsViewModel(
            imageSharer = imageSharer,
            transactionRepository = transactionRepository,
            ioDispatcher = testDispatcher
        )

        viewModel.state.test {
            skipItems(2)

            viewModel.onScreenShotCaptured(byteArray, "test_file")

            val finalState = awaitItem()
            assertTrue(!finalState.isShareReceiptBtnLoading)
        }
    }

    @Test
    fun `onScreenShotCaptured should show error snack bar when fail`() = runTest {
        everySuspend { transactionRepository.getTransactionDetails(any()) } returns transaction1
        everySuspend { imageSharer.shareImage(any(), any(), any()) } throws Exception()

        val byteArray = byteArrayOf()
        val viewModel = TransactionDetailsViewModel(
            imageSharer = imageSharer,
            transactionRepository = transactionRepository,
            ioDispatcher = testDispatcher
        )

        viewModel.state.test {
            skipItems(3)
            viewModel.onScreenShotCaptured(byteArray, "test_file")
            val finalState = awaitItem()
            assertEquals(true, finalState.snackBar.isSuccess)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private companion object {
        val transaction1Id = Uuid.random()
        val transaction1 = Transaction(
            id = transaction1Id,
            createdAt = LocalDateTime(
                date = LocalDate(2025, 8, 20),
                time = LocalTime(12, 0)
            ),
            amount = 5000.0,
            status = Transaction.Status.SUCCESS,
            senderId = Uuid.random(),
            senderName = "Nour Elhoda",
            receiverId = Uuid.random(),
            receiverName = "Nour Elhoda",
            type = Transaction.Type.RECEIVED
        )
        val transaction1uiState = TransactionDetailsScreenState.TransactionDetailsUiState(
            id = transaction1Id.toString(),
            amount = "5000.0",
            date = "20 Aug 2025, 12:00 PM",
            userName = "Nour Elhoda",
            otherParty = "Nour Elhoda",
            transactionType = Transaction.Type.RECEIVED,
            transactionStatus = Transaction.Status.SUCCESS
        )
    }
}