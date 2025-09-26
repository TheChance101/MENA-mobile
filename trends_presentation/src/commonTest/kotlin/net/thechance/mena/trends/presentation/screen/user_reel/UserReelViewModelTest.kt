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
    private lateinit var viewModel: UserReelViewModel
    private val mockReelsRepository: ReelsRepository = mock(MockMode.autofill)
    private val userReelArgs: UserReelArgs = mock(MockMode.autofill)


    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { userReelArgs.realId } returns "1"

        everySuspend { mockReelsRepository.deleteReelById("1") }

        viewModel = UserReelViewModel(userReelArgs, mockReelsRepository)
    }

    @Test
    fun `should initialize UserReelUiState with default state`() = runTest {
        // When
        viewModel.state.test {
            val initialState = awaitItem()

            // Then
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
    fun `should expand description when onDescriptionClick is called with collapsed state`() = runTest {
        // When
        viewModel.onDescriptionClick(isCollapsed = false)

        // Then
         viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isDescriptionExpanded)
             cancelAndIgnoreRemainingEvents()

         }
    }

    @Test
    fun `should collapse description when onDescriptionClick is called with expanded state`() = runTest {
        // When
        viewModel.onDescriptionClick(isCollapsed = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isDescriptionExpanded)
            cancelAndIgnoreRemainingEvents()

        }
    }

    @Test
    fun `should send NavigateBack effect when onBackClick is called`() = runTest {
        // When
        viewModel.onBackClick()

        // Then
        viewModel.effect.test {
            val effect = awaitItem()
            assertTrue(effect is UserReelEffect.NavigateBack)
            cancelAndIgnoreRemainingEvents()

        }
    }

    @Test
    fun `should show confirmation dialog when onDeleteClick is called`() = runTest {
        // When
        viewModel.onDeleteClick()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isConfirmationDialogVisible)
            cancelAndIgnoreRemainingEvents()

        }
    }

    @Test
    fun `should dismiss confirmation dialog when onDismissConfirmationDialog is called`() = runTest {
        // When
        viewModel.onDeleteClick()

        // Then
        viewModel.onDismissConfirmationDialog()
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isConfirmationDialogVisible)
            cancelAndIgnoreRemainingEvents()

        }
    }

    @Test
    fun `should delete reel successfully when onConfirmDeleteClick is called`() = runTest {
        // When
        viewModel.onConfirmDeleteClick()

        // Then
        viewModel.state.test {
            awaitItem()
            val state2 = awaitItem()

            assertFalse(state2.isConfirmationDialogVisible)

            assertEquals(true ,state2.isReelDeleted)
            cancelAndIgnoreRemainingEvents()

        }
    }

    @Test
    fun `should dismiss success dialog when onDismissSuccessDialog is called`() = runTest {
        // When
        viewModel.onDismissSuccessDialog()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.isReelDeleted)
            assertFalse(state.isConfirmationDialogVisible)
            cancelAndIgnoreRemainingEvents()

        }
    }

    @Test
    fun `should dismiss error dialog when onDismissErrorDialog is called`() = runTest {
        // When
        viewModel.onDismissErrorDialog()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isConfirmationDialogVisible)
            cancelAndIgnoreRemainingEvents()

        }
    }

    @Test
    fun `onConfirmDeleteClick should update error state when repository throws exception`() = runTest {
        // Given
        val errorMessage = "Delete failed"
        everySuspend { mockReelsRepository.deleteReelById("1") } throws Exception(errorMessage)

        // When
        viewModel.onConfirmDeleteClick()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
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