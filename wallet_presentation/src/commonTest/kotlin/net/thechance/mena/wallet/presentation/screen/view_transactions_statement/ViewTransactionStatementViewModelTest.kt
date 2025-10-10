package net.thechance.mena.wallet.presentation.screen.view_transactions_statement

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
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
import net.thechance.mena.wallet.domain.repository.StatementRepository
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.base.UiState
import net.thechance.mena.wallet.presentation.base.UiState.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ViewTransactionStatementViewModelTest {
    private val repository = mock<StatementRepository>(mode = MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ViewTransactionStatementViewModel

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
            viewModel = ViewTransactionStatementViewModel(repository, testDispatcher)

            viewModel.uiEffect.test {
                viewModel.onNavigateBackClicked()
                advanceUntilIdle()

                val effect = awaitItem()
                assertTrue(effect is ViewTransactionStatementEffect.NavigateBack)
            }
        }

    @Test
    fun `initialization should fetch statement`() = runTest(testDispatcher) {
        everySuspend { repository.getTransactionsPdf(null) } returns statement

        initViewModel()

        val finalState = viewModel.state.value
        assertTrue(finalState.statement is Success)
        assertContentEquals(statement, finalState.statement.data)
    }

    @Test
    fun `onShareClicked should send ShareStatement effect when called`() = runTest(testDispatcher) {
        everySuspend { repository.getTransactionsPdf(null) } returns statement

        initViewModel()

        viewModel.uiEffect.test {
            viewModel.onShareClicked()
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is ViewTransactionStatementEffect.ShareStatement)
        }
    }

    @Test
    fun `onShareClicked should send ShareStatement effect with statement when called`() =
        runTest(testDispatcher) {
            everySuspend { repository.getTransactionsPdf(null) } returns statement

            initViewModel()

            viewModel.uiEffect.test {
                viewModel.onShareClicked()
                advanceUntilIdle()

                val effect = awaitItem()
                val effectStatement =
                    (effect as ViewTransactionStatementEffect.ShareStatement).statement
                assertContentEquals(statement, effectStatement)
            }
        }

    @Test
    fun `initialization should save the error in the state when an error occurs while fetching the statement`() =
        runTest(testDispatcher) {
            everySuspend { repository.getTransactionsPdf(null) } throws Exception()

            initViewModel()
            val finalState = viewModel.state.value
            assertTrue(finalState.statement is Error)
        }

    @Test
    fun `onShareClicked should not send ShareStatement effect when statement is not available`() =
        runTest(testDispatcher) {
            everySuspend { repository.getTransactionsPdf(null) } throws Exception()
            initViewModel()
            viewModel.uiEffect.test {
                viewModel.onShareClicked()
                advanceUntilIdle()
                expectNoEvents()
            }
        }

    @Test
    fun `state should update with NoDataFound error when the fetched statement is empty`() =
        runTest(testDispatcher) {
            everySuspend { repository.getTransactionsPdf(null) } returns ByteArray(0)

            initViewModel()

            val finalState = viewModel.state.value
            assertTrue(finalState.statement is Success)
            assertTrue(finalState.statement.data.isEmpty())
        }

    private fun TestScope.initViewModel() {
        viewModel = ViewTransactionStatementViewModel(repository, testDispatcher)
        viewModel.getStatementPdf(null)
        advanceUntilIdle()
    }

    private companion object {
        val statement = ByteArray(5) { it.toByte() }
    }
}