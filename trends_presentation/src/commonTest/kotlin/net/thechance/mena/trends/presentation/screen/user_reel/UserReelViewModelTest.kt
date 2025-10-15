package net.thechance.mena.trends.presentation.screen.user_reel

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.presentation.screen.user_reel.args.UserReelArgs
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class UserReelViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val mockReelsRepository: ReelsRepository = mock(MockMode.autofill) {
        everySuspend { deleteReelById("1") } returns Unit
    }

    private val userReelArgs: UserReelArgs = mock(MockMode.autofill) {
        every { realId } returns "1"
    }

    private val viewModel by lazy {
        UserReelViewModel(userReelArgs, mockReelsRepository, testDispatcher)
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `should initialize UserReelUiState with default state`() = runTest {
        viewModel.state.test {
            val initialState = awaitItem()

            assertFalse(initialState.isLoading)
            assertNull(initialState.error)
            assertEquals("Istanbul Squad", initialState.username)
            assertFalse(initialState.isConfirmationDialogVisible)
            assertNull(initialState.isReelDeleted)
            assertFalse(initialState.isDescriptionExpanded)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDescriptionClick should expand description when called with collapsed state`() = runTest {
        viewModel.onDescriptionClick(isCollapsed = false)

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isDescriptionExpanded)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDescriptionClick should collapse description when called with expanded state`() = runTest {
        viewModel.onDescriptionClick(isCollapsed = true)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isDescriptionExpanded)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onBackClick should send NavigateBack effect when called`() = runTest {
        viewModel.onBackClick()

        viewModel.effect.test {
            val effect = awaitItem()
            assertTrue(effect is UserReelEffect.NavigateBack)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDeleteClick should show confirmation dialog when called`() = runTest {
        viewModel.onDeleteClick()

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isConfirmationDialogVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDismissConfirmationDialog should dismiss confirmation dialog when called`() = runTest {
        viewModel.onDeleteClick()

        viewModel.onDismissConfirmationDialog()
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isConfirmationDialogVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onConfirmDeleteClick should delete reel successfully when is called`() = runTest {
        viewModel.onConfirmDeleteClick()

        viewModel.state.test {
            skipItems(1)
            val state2 = awaitItem()

            assertFalse(state2.isConfirmationDialogVisible)

            assertEquals(true ,state2.isReelDeleted)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDismissSuccessDialog should dismiss success dialog when called`() = runTest {
        viewModel.onDismissSuccessDialog()

        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.isReelDeleted)
            assertFalse(state.isConfirmationDialogVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDismissErrorDialog should dismiss error dialog when called`() = runTest {
        viewModel.onDismissErrorDialog()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isConfirmationDialogVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onConfirmDeleteClick should update error state when repository throws exception`() = runTest {

        val errorMockRepository: ReelsRepository = mock(MockMode.autofill) {
            everySuspend { deleteReelById("1") } throws Exception()
        }

        val errorViewModel = UserReelViewModel(userReelArgs, errorMockRepository, testDispatcher)

        errorViewModel.onConfirmDeleteClick()
        testDispatcher.scheduler.advanceUntilIdle()

        errorViewModel.state.test {
            val errorState = awaitItem()
            assertNotNull(errorState.error is ErrorState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
}