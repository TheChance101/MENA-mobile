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
import net.thechance.mena.wallet.domain.entity.TransactionStatus
import net.thechance.mena.wallet.domain.entity.TransactionType
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.screen.transaction_details.args.TransactionDetailsArgs
import net.thechance.mena.wallet.presentation.utils.StringProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionDetailsViewModelTest {
    private val stringProvider = mock<StringProvider>(mode = MockMode.autofill)
    private val transactionRepository = mock<TransactionRepository>(mode = MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()
    private val transactionDetailsArgs: TransactionDetailsArgs = object : TransactionDetailsArgs {
        override val id: String
            get() = transaction1Id.toString()
    }

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

            val viewModel = viewmodelSetup()

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

            val viewModel = viewmodelSetup()

            viewModel.state.test {
                skipItems(2)
                val successState = awaitItem()
                assertEquals(
                    transaction1uiState.id,
                    successState.transactionDetailsUiState.id
                )
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

            val viewModel = viewmodelSetup()

            viewModel.state.test {
                skipItems(2)
                val errorState = awaitItem()
                assertEquals(ErrorState.UnknownError, errorState.errorState)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onBackButtonClicked should send NavigateBack effect`() = runTest {
        everySuspend { transactionRepository.getTransactionById(any()) } returns transaction1

        val viewModel = viewmodelSetup()

        viewModel.uiEffect.test {
            viewModel.onBackButtonClicked()
            assertEquals(TransactionDetailsEffect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `onShareReceiptButtonClicked should set loading state to true when success`() =
        runTest {
            everySuspend { transactionRepository.getTransactionById(any()) } returns transaction1

            val viewModel = viewmodelSetup()

            viewModel.state.test {
                skipItems(1)
                viewModel.onShareReceiptButtonClicked()
                val finalState = awaitItem()
                assertTrue(finalState.isShareReceiptBtnLoading)
            }
        }

    @Test
    fun `onShareReceiptButtonClicked should send CaptureImage effect`() =
        runTest {
            everySuspend { transactionRepository.getTransactionById(any()) } returns transaction1

            val viewModel = viewmodelSetup()

            viewModel.uiEffect.test {
                viewModel.onShareReceiptButtonClicked()
                assertEquals(TransactionDetailsEffect.CaptureImage, awaitItem())
            }
        }

    @Test
    fun `onScreenShotCaptured should reset loading state to false when success`() =
        runTest {
            everySuspend { transactionRepository.getTransactionById(any()) } returns transaction1

            val byteArray = byteArrayOf()
            val viewModel = viewmodelSetup()

            viewModel.state.test {
                skipItems(2)

                viewModel.onScreenShotCaptured(byteArray, "test_file")

                val finalState = awaitItem()
                assertTrue(!finalState.isShareReceiptBtnLoading)
            }
        }

    @Test
    fun `onScreenShotCaptured should send ShareImage effect`() =
        runTest {
            everySuspend { transactionRepository.getTransactionById(any()) } returns transaction1

            val byteArray = byteArrayOf()
            val viewModel = viewmodelSetup()

            viewModel.uiEffect.test {
                viewModel.onScreenShotCaptured(byteArray, "test_file")
                assertEquals(
                    TransactionDetailsEffect.ShareImage(
                        byteArray,
                        "test_file.png",
                        "image/png"
                    ),
                    awaitItem()
                )
            }
        }

    @Test
    fun `onScreenShotCaptured should reset loading state to false when called`() = runTest {
        everySuspend { transactionRepository.getTransactionById(any()) } returns transaction1

        val byteArray = byteArrayOf()
        val viewModel = viewmodelSetup()

        viewModel.state.test {
            skipItems(1)

            viewModel.onScreenShotCaptured(byteArray, "test_file")

            val finalState = awaitItem()
            assertTrue(!finalState.isShareReceiptBtnLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onScreenShotCaptured should show error snack bar when fail`() = runTest {
        everySuspend { transactionRepository.getTransactionById(any()) } returns transaction1

        val byteArray = byteArrayOf()
        val viewModel = viewmodelSetup()

        viewModel.state.test {
            skipItems(2)
            viewModel.onScreenShotCaptured(byteArray, "test_file")
            val finalState = awaitItem()
            assertEquals(true, finalState.snackBar.isSuccess)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onCaptureError should show error snackbar`() = runTest {
        everySuspend { transactionRepository.getTransactionById(any()) } returns transaction1

        val viewModel = viewmodelSetup()

        viewModel.state.test {
            skipItems(2)

            viewModel.onCaptureError()

            val finalState = awaitItem()
            assertEquals(true, finalState.snackBar.isSuccess)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onRefresh should set transaction with loading when initially called`() = runTest {
        everySuspend { transactionRepository.getTransactionById(any()) } returns transaction1

        val viewModel = viewmodelSetup()

        viewModel.state.test {
            skipItems(3)
            viewModel.onRefresh()
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun viewmodelSetup() = TransactionDetailsViewModel(
        transactionRepository = transactionRepository,
        transactionDetailsArgs = transactionDetailsArgs,
        dispatcher = testDispatcher,
        stringProvider = stringProvider
    )

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
            senderImageUrl = "",
            receiverImageUrl = "",
            receiverName = "Nour Elhoda",
            type = TransactionType.RECEIVED
        )
        val transaction1uiState = TransactionDetailsScreenState.TransactionDetailsUiState(
            id = "TX-${transaction1Id.toString().substring(0, 6)}",
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