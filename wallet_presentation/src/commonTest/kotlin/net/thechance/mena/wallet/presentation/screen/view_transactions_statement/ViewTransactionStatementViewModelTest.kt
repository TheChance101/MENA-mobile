package net.thechance.mena.wallet.presentation.screen.view_transactions_statement

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.wallet.domain.repository.StatementRepository
import net.thechance.mena.wallet.presentation.base.UiState
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
        viewModel = ViewTransactionStatementViewModel(repository)
    }

    @AfterTest
    fun terDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onNavigateBackClicked should send NavigateBack effect when called`() = runTest {
        viewModel.onNavigateBackClicked()

        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertTrue(effect is ViewTransactionStatementEffect.NavigateBack)
        }
    }

    @Test
    fun `should fetch statement when initialized`() = runTest {
        everySuspend { repository.getLastStatement() } returns statement
        viewModel = ViewTransactionStatementViewModel(repository, testDispatcher)

        viewModel.state.test {
            skipItems(1)
            val state = awaitItem()
            assertContentEquals((state.statement as UiState.Success).data, statement)
        }
    }

    @Test
    fun `onShareClicked should send ShareStatement effect when called`() = runTest {
        viewModel.onShareClicked()

        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertTrue(effect is ViewTransactionStatementEffect.ShareStatement)
        }
    }

    @Test
    fun `onShareClicked should send ShareStatement with statement effect when called`() = runTest {
        everySuspend { repository.getLastStatement() } returns statement
        viewModel = ViewTransactionStatementViewModel(repository, testDispatcher)
        advanceUntilIdle()

        viewModel.onShareClicked()

        viewModel.uiEffect.test {
            val effect = awaitItem()
            val effectStatement = (effect as ViewTransactionStatementEffect.ShareStatement).statement
            assertContentEquals(statement, effectStatement)
        }
    }

    private companion object {
        val statement = ByteArray(5, { it.toByte() })
    }
}