@file:OptIn(ExperimentalUuidApi::class, ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.transaction_details

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
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.from
import mena.wallet_presentation.generated.resources.to
import mena.wallet_presentation.generated.resources.transfer
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.model.TransactionStatus
import net.thechance.mena.wallet.domain.model.TransactionType
import net.thechance.mena.wallet.domain.repository.TransactionRepository
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
    fun `getTransactionDetails should set transaction with loading when initially called`() =
        runTest {
            everySuspend { transactionRepository.getTransactionById(any()) } returns transaction1

            val viewModel = TransactionDetailsViewModel(
                imageSharer = imageSharer,
                transactionRepository = transactionRepository,
                transactionId = transaction1Id.toString(),
                ioDispatcher = testDispatcher
            )

            viewModel.state.test {
                skipItems(1)
                val initialState = awaitItem()
                assertTrue(initialState.isLoading)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getTransactionDetails should update transaction ui state with success when repository returns value`() =
        runTest {
            everySuspend { transactionRepository.getTransactionById(any()) } returns transaction1

            val viewModel = TransactionDetailsViewModel(
                imageSharer = imageSharer,
                transactionRepository = transactionRepository,
                transactionId = transaction1Id.toString(),
                ioDispatcher = testDispatcher
            )

            viewModel.state.test {
                skipItems(2)
                val successState = awaitItem()
                assertEquals(
                    transaction1uiState.transactionType,
                    successState.transactionDetailsUiState.transactionType
                )
                assertEquals(
                    transaction1uiState.userInfo,
                    successState.transactionDetailsUiState.userInfo
                )
                assertEquals(
                    transaction1uiState.typeContent,
                    successState.transactionDetailsUiState.typeContent
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getTransactionDetails should update transaction ui state with error when repository fails`() =
        runTest {
            val expectedError = Exception()
            everySuspend { transactionRepository.getTransactionById(any()) } throws expectedError

            val viewModel = TransactionDetailsViewModel(
                imageSharer = imageSharer,
                transactionRepository = transactionRepository,
                transactionId = transaction1Id.toString(),
                ioDispatcher = testDispatcher
            )

            viewModel.state.test {
                skipItems(2)
                val errorState = awaitItem()
                assertEquals(expectedError, errorState.isError)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onBackButtonClicked should send NavigateBack effect`() = runTest {
        everySuspend { transactionRepository.getTransactionById(any()) } returns transaction1

        val viewModel = TransactionDetailsViewModel(
            imageSharer = imageSharer,
            transactionRepository = transactionRepository,
            transactionId = transaction1Id.toString(),
            ioDispatcher = testDispatcher
        )

        viewModel.uiEffect.test {
            viewModel.onBackButtonClicked()
            assertEquals(TransactionDetailsEffect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `onScreenShotCaptured should share image and reset loading state to false when success`() =
        runTest {
            everySuspend { transactionRepository.getTransactionById(any()) } returns transaction1
            everySuspend { imageSharer.shareImage(any(), any(), any()) } returns Unit

            val byteArray = byteArrayOf()
            val viewModel = TransactionDetailsViewModel(
                imageSharer = imageSharer,
                transactionRepository = transactionRepository,
                transactionId = transaction1Id.toString(),
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
        everySuspend { transactionRepository.getTransactionById(any()) } returns transaction1
        everySuspend { imageSharer.shareImage(any(), any(), any()) } throws Exception()

        val byteArray = byteArrayOf()
        val viewModel = TransactionDetailsViewModel(
            imageSharer = imageSharer,
            transactionRepository = transactionRepository,
            transactionId = transaction1Id.toString(),
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
        everySuspend { transactionRepository.getTransactionById(any()) } returns transaction1
        everySuspend { imageSharer.shareImage(any(), any(), any()) } throws Exception()

        val byteArray = byteArrayOf()
        val viewModel = TransactionDetailsViewModel(
            imageSharer = imageSharer,
            transactionRepository = transactionRepository,
            transactionId = transaction1Id.toString(),
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

    @Test
    fun `onShareReceiptButtonClicked should reset loading state to false when success`() = runTest {
        everySuspend { transactionRepository.getTransactionById(any()) } returns transaction1

        val viewModel = TransactionDetailsViewModel(
            imageSharer = imageSharer,
            transactionRepository = transactionRepository,
            transactionId = transaction1Id.toString(),
            ioDispatcher = testDispatcher
        )

        viewModel.state.test {
            skipItems(2)

            viewModel.onShareReceiptButtonClicked({})

            val finalState = awaitItem()
            assertTrue(!finalState.isShareReceiptBtnLoading)
        }
    }

    @Test
    fun `onRefresh should set transaction with loading when initially called`() = runTest {
        everySuspend { transactionRepository.getTransactionById(any()) } returns transaction1

        val viewModel = TransactionDetailsViewModel(
            imageSharer = imageSharer,
            transactionRepository = transactionRepository,
            transactionId = transaction1Id.toString(),
            ioDispatcher = testDispatcher
        )

        viewModel.state.test {
            skipItems(3)
            viewModel.onRefresh()
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)
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
            status = TransactionStatus.SUCCESS,
            senderName = "Nour Elhoda",
            receiverName = "Nour Elhoda",
            type = TransactionType.RECEIVED
        )
        val transaction1uiState = TransactionDetailsScreenState.TransactionDetailsUiState(
            id = transaction1Id.toString(),
            amount = "5000.0",
            date = "20 Aug 2025, 12:00 pm",
            userName = "Nour Elhoda",
            otherParty = "Nour Elhoda",
            transactionType = TransactionDetailsScreenState.TransactionTypeUiState.RECEIVED,
            transactionStatus = TransactionDetailsScreenState.TransactionStatusUiState.SUCCESS,
            userInfo = Res.string.to,
            typeContent = Res.string.transfer,
            otherPartyTitle = Res.string.from
        )
    }
}