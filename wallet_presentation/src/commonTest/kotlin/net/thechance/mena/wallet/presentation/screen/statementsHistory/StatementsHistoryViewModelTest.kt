@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.statementsHistory

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import net.thechance.mena.wallet.domain.entity.Statement
import net.thechance.mena.wallet.domain.exceptions.NoInternetException
import net.thechance.mena.wallet.domain.exceptions.UnknownException
import net.thechance.mena.wallet.domain.repository.StatementRepository
import net.thechance.mena.wallet.presentation.base.ErrorState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class StatementsHistoryViewModelTest {
    private val statementRepository = mock<StatementRepository>(mode = MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: StatementsHistoryViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        everySuspend { statementRepository.getStatements(any(), any()) } returns emptyList()
        viewModel = StatementsHistoryViewModel(statementRepository, testDispatcher)
    }

    @AfterTest
    fun terDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onNavigateBackClicked should send NavigateBack effect when called`() =
        runTest(testDispatcher) {
            viewModel.onBackClicked()

            viewModel.uiEffect.test {
                val effect = awaitItem()
                assertTrue(effect is StatementsHistoryEffect.NavigateBack)
            }
        }

    @Test
    fun `onStatementCardClicked should send NavigateToStatementDetails effect`() =
        runTest(testDispatcher) {
            everySuspend {
                statementRepository.getStatements(PAGE, PAGE_SIZE)
            } returns emptyList()
            val id = 1L
            advanceUntilIdle()
            viewModel.uiEffect.test {
                viewModel.onStatementCardClicked(id)
                val effect = awaitItem()
                assertEquals(StatementsHistoryEffect.NavigateToStatementDetails(id), effect)
                cancelAndIgnoreRemainingEvents()
            }

        }

    @Test
    fun `onRetryLoadStatementsHistoryClicked should reload statements`() = runTest(testDispatcher) {
        everySuspend { statementRepository.getStatements(any(), any()) } returns emptyList()

        viewModel.onRetryLoadStatementsHistoryClicked()
        advanceUntilIdle()

        verifySuspend { statementRepository.getStatements(any(), any()) }
    }

    @Test
    fun `onNextPageRequested should load next page of statements`() = runTest(testDispatcher) {
        everySuspend { statementRepository.getStatements(any(), any()) } returns emptyList()

        viewModel.onNextPageRequested()
        advanceUntilIdle()

        verifySuspend {
            statementRepository.getStatements(any(), any())
        }
    }

    @Test
    fun `paginator should load statements successfully on first page`() = runTest(testDispatcher) {
        val mockStatements = statements

        everySuspend { statementRepository.getStatements(0, 20) } returns mockStatements

        val viewModel = StatementsHistoryViewModel(statementRepository, testDispatcher)

        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(3, state.statements.size)
            assertFalse(state.isLoading)
            assertFalse(state.endOfPages)
            assertNull(state.errorState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `paginator should set endOfPages when empty result is returned`() =
        runTest(testDispatcher) {
            everySuspend { statementRepository.getStatements(0, 20) } returns emptyList()

            val viewModel = StatementsHistoryViewModel(statementRepository, testDispatcher)

            advanceUntilIdle()
            viewModel.state.test {
                val state = awaitItem()
                assertTrue(state.endOfPages)
                assertTrue(state.statements.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `paginator should handle NoInternetException and set error state`() =
        runTest(testDispatcher) {
            everySuspend { statementRepository.getStatements(0, 20) } throws NoInternetException()

            val viewModel = StatementsHistoryViewModel(statementRepository, testDispatcher)

            advanceUntilIdle()
            viewModel.state.test {
                val state = awaitItem()
                assertEquals(ErrorState.NoInternet, state.errorState)
                assertFalse(state.isLoading)
                assertTrue(state.statements.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `paginator should handle UnknownException and set error state`() = runTest(testDispatcher) {
        everySuspend { statementRepository.getStatements(0, 20) } throws UnknownException()

        val viewModel = StatementsHistoryViewModel(statementRepository, testDispatcher)

        advanceUntilIdle()
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(ErrorState.Unknown, state.errorState)
            assertFalse(state.isLoading)
            assertTrue(state.statements.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    companion object {
        val statements = listOf(
            Statement(
                1,
                LocalDate(2025, 3, 1),
                LocalDate(2025, 3, 31),
                1410.0,
                990.0,
                "/storage/statements/mar_2025.pdf"
            ),
            Statement(
                2,
                LocalDate(2025, 4, 1),
                LocalDate(2025, 4, 30),
                1600.3,
                1120.4,
                "/storage/statements/apr_2025.pdf"
            ),
            Statement(
                3,
                LocalDate(2025, 5, 1),
                LocalDate(2025, 5, 31),
                1555.0,
                1080.5,
                "/storage/statements/may_2025.pdf"
            )
        )
        const val PAGE_SIZE = 20
        const val PAGE = 1
    }

}