package net.thechance.mena.trends.presentation.manage_my_trends

import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.trends.domain.entity.Category
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.presentation.screen.manage_my_trends.ManageTrendsUiEffect
import net.thechance.mena.trends.presentation.screen.manage_my_trends.ManageTrendsViewModel
import net.thechance.mena.trends.presentation.screen.manage_my_trends.ReelUiState
import net.thechance.mena.trends.presentation.screen.manage_my_trends.toUiState
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ManageTrendsViewModelTest {
    private val repository: ReelsRepository = mock(MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `view model should update state by reels when getAllReels returns data`() =
        runTest(testDispatcher) {
            // Given
            everySuspend { repository.getAllReels(1) } returns reelList

            // When
            val viewModel = ManageTrendsViewModel(repository)
            testScheduler.advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(false, currentState.isLoading)
                assertNotNull(currentState.reels)
                assertEquals(null, currentState.error)

                val reelsSnapshot: List<ReelUiState> = currentState.reels.asSnapshot()
                assertEquals(expectedReelUiStateList, reelsSnapshot)
            }
        }

    @Test
    fun `initialize view model should handle error state when getAllReels fails`() =
        runTest(testDispatcher) {
            // Given
            val errorMessage = "Network error"
            everySuspend { repository.getAllReels(1) } throws Exception(errorMessage)

            // When
            val viewModel = ManageTrendsViewModel(repository)
            testScheduler.advanceUntilIdle()

            // Then
            viewModel.state.test {
                val currentState = awaitItem()
                assertEquals(false, currentState.isLoading)
                assertNull(currentState.error)
            }
            assertFailsWith<Exception> {
                viewModel.state.value.reels?.asSnapshot()
            }
        }

    @Test
    fun `getReels should set loading state during execution`() = runTest(testDispatcher) {
        // Given
        everySuspend { repository.getAllReels(1) } returns reelList
        val viewModel = ManageTrendsViewModel(repository)
        testScheduler.advanceUntilIdle()

        // When & Then
        viewModel.state.test {
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)

            viewModel.getReels()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            testScheduler.advanceUntilIdle()

            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
        }
    }

    @Test
    fun `onReelItemClick should navigate to trend screen with reel id`() = runTest(testDispatcher) {
        // Given
        everySuspend { repository.getAllReels(1) } returns emptyList()
        val viewModel = ManageTrendsViewModel(repository)
        testScheduler.advanceUntilIdle()

        // When & Then
        viewModel.effect.test {
            viewModel.onReelItemClick(REEL_ID)
            assertEquals(ManageTrendsUiEffect.NavigateToTrend(REEL_ID), awaitItem())
        }
    }

    @Test
    fun `onBackClick should navigate back`() = runTest(testDispatcher) {
        // Given
        everySuspend { repository.getAllReels(1) } returns emptyList()
        val viewModel = ManageTrendsViewModel(repository)
        testScheduler.advanceUntilIdle()

        // When & Then
        viewModel.effect.test {
            viewModel.onBackClick()
            assertEquals(ManageTrendsUiEffect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `viewModel should start with initial state`() = runTest(testDispatcher) {
        // Given
        everySuspend { repository.getAllReels(1) } returns reelList

        // When
        val viewModel = ManageTrendsViewModel(repository)

        // Then
        val initialState = viewModel.state.value
        assertNotNull(initialState)
        assertTrue(initialState.isLoading)
    }

    @Test
    fun `getReels should trigger repository call and handle success`() = runTest(testDispatcher) {
        // Given
        everySuspend { repository.getAllReels(1) } returns reelList
        val viewModel = ManageTrendsViewModel(repository)
        testScheduler.advanceUntilIdle()

        // When
        viewModel.getReels()
        testScheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val currentState = awaitItem()
            assertEquals(false, currentState.isLoading)
            assertEquals(null, currentState.error)
            assertNotNull(currentState.reels)

            val reelsSnapshot: List<ReelUiState> = currentState.reels.asSnapshot()
            assertEquals(expectedReelUiStateList, reelsSnapshot)
        }
    }

    @Test
    fun `toUiState extension function should map correctly`() {
        // Given
        val reel = reelList[0]

        // When
        val uiState = reel.toUiState()

        // Then
        assertEquals("1", uiState.id)
        assertEquals("thumb1.jpg", uiState.thumbnailUrl)
    }

    private companion object {
        const val REEL_ID = "1"
        val reelList = listOf(
            Reel(
                id = "1",
                thumbnailUrl = "thumb1.jpg",
                videoUrl = "video1.mp4",
                description = "First reel",
                likesCount = 100,
                viewsCount = 1000,
                createdAt = LocalDateTime(2023, 10, 1, 12, 0),
                categories = listOf(Category("1", "Trend", ":fire:"))
            ),
            Reel(
                id = "2",
                thumbnailUrl = "thumb2.jpg",
                videoUrl = "video2.mp4",
                description = "Second reel",
                likesCount = 200,
                viewsCount = 2000,
                createdAt = LocalDateTime(2023, 10, 2, 12, 0),
                categories = listOf(Category("2", "Viral", ":rocket:"))
            )
        )

        val expectedReelUiStateList = listOf(
            ReelUiState(
                id = "1",
                thumbnailUrl = "thumb1.jpg",
            ),
            ReelUiState(
                id = "2",
                thumbnailUrl = "thumb2.jpg",
            )
        )
    }
}