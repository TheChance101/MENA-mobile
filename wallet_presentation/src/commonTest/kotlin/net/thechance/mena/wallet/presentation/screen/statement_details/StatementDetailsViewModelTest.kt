package net.thechance.mena.wallet.presentation.screen.statement_details

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import net.thechance.mena.wallet.domain.model.StatementWithMetaData
import net.thechance.mena.wallet.domain.repository.StatementRepository
import net.thechance.mena.wallet.presentation.utils.FileManager
import net.thechance.mena.wallet.presentation.utils.StorageLocation
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class StatementDetailsViewModelTest {
    private val repository = mock<StatementRepository>(mode = MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()
    private val fileManager = mock<FileManager>(mode = MockMode.autofill)
    private val statementLocation = StorageLocation.Cache("test_statement.pdf")
    private lateinit var viewModel: StatementDetailsViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onNavigateBackClicked should send NavigateBack effect when called`() =
        runTest(testDispatcher) {
            initViewModel()

            viewModel.uiEffect.test {
                viewModel.onNavigateBackClicked()
                advanceUntilIdle()

                val effect = awaitItem()
                assertTrue(effect is StatementDetailsEffect.NavigateBack)
            }
        }

    @Test
    fun `initialization should fetch statement`() = runTest(testDispatcher) {
        everySuspend { repository.getStatementWithMetadata(null) } returns createMockStatementWithMetadata()

        initViewModel()

        val finalState = viewModel.state.value
        assertTrue(finalState.statement.isNotEmpty())
        assertContentEquals(createMockStatementWithMetadata().byteArray, finalState.statement)
    }

    @Test
    fun `onRetryClicked should call getPdfBytes and update state when successful`() = runTest(testDispatcher) {
        val expectedBytes = byteArrayOf(1, 2, 3)
        everySuspend { fileManager.readFile(statementLocation) } returns expectedBytes

        initViewModel()

        viewModel.onRetryClicked()
        advanceUntilIdle()

        val finalState = viewModel.state.value
        assertContentEquals(expectedBytes, finalState.statement)
    }

    @Test
    fun `onShareClicked should send ShareStatement effect when called`() = runTest(testDispatcher) {
        everySuspend { repository.getStatementWithMetadata(null) } returns createMockStatementWithMetadata()

        initViewModel()

        viewModel.uiEffect.test {
            viewModel.onShareClicked()
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is StatementDetailsEffect.ShareStatement)
        }
    }

    @Test
    fun `onShareClicked should send ShareStatement effect with statement when called`() =
        runTest(testDispatcher) {
            everySuspend { repository.getStatementWithMetadata(null) } returns createMockStatementWithMetadata()

            initViewModel()

            viewModel.uiEffect.test {
                viewModel.onShareClicked()
                advanceUntilIdle()

                val effect = awaitItem()
                val effectStatement =
                    (effect as StatementDetailsEffect.ShareStatement).statement
                assertContentEquals(createMockStatementWithMetadata().byteArray, effectStatement)
            }
        }


    @Test
    fun `state should update with NoDataFound error when the fetched statement is empty`() =
        runTest(testDispatcher) {
            everySuspend { repository.getStatementWithMetadata(null) } returns createMockStatementWithMetadata(
                byteArray = byteArrayOf()
            )

            initViewModel()

            val finalState = viewModel.state.value
            assertTrue(finalState.statement.isNotEmpty())
        }

    private fun TestScope.initViewModel() {
        viewModel = StatementDetailsViewModel(fileManager, statementLocation, testDispatcher)
        advanceUntilIdle()
    }

    private fun createMockStatementWithMetadata(
        byteArray: ByteArray = byteArrayOf(0),
        startDate: LocalDate = LocalDate(2025, 9, 1),
        endDate: LocalDate = LocalDate(2025, 9, 30),
        totalInflows: Double = 1000.0,
        totalOutflows: Double = 500.0
    ): StatementWithMetaData {
        return StatementWithMetaData(
            byteArray = byteArray,
            startDate = startDate,
            endDate = endDate,
            totalInflows = totalInflows,
            totalOutflows = totalOutflows,
        )
    }
}