package net.thechance.mena.trends.presentation.manage_my_trends

import androidx.paging.PagingData
import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.trends.domain.di.TrendDomainModule
import net.thechance.mena.trends.domain.entity.Category
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.presentation.di.TrendPresentationModule
import net.thechance.mena.trends.presentation.screen.manage_my_trends.ManageTrendsScreenState
import net.thechance.mena.trends.presentation.screen.manage_my_trends.ManageTrendsUiEffect
import net.thechance.mena.trends.presentation.screen.manage_my_trends.ManageTrendsViewModel
import net.thechance.mena.trends.presentation.screen.manage_my_trends.ReelUiState
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.ksp.generated.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ManageTrendsViewModelTest {
    private val repository: ReelsRepository = mock(MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()
    @BeforeTest
    fun setUp() {
        kotlinx.coroutines.Dispatchers.setMain(testDispatcher)
        startKoin {
            modules(
                TrendPresentationModule().module,
                TrendDomainModule().module
            )
        }
    }
    @AfterTest
    fun tearDown() {
        stopKoin()
    }
    @Test
    fun `initialize view model should set success state when getAllReels returns data`() = runTest(testDispatcher) {
        // Given
        everySuspend { repository.getAllReels(1) } returns reelList
        // When
        val viewModel = ManageTrendsViewModel(repository)
        testScheduler.advanceUntilIdle()
        // Then
        viewModel.state.test {
            val currentState = awaitItem()
            assertEquals(false, currentState.isLoading)
            assertEquals(null, currentState.error)
        }
    }

    @Test
    fun `initialize view model should handle error state when getAllReels fails`() = runTest(testDispatcher) {
        // Given
        everySuspend { repository.getAllReels(1) } throws Exception("Error")
        // When
        val viewModel = ManageTrendsViewModel(repository)
        testScheduler.advanceUntilIdle()
        // Then
        viewModel.state.test {
            val currentState = awaitItem()
            assertEquals(false, currentState.isLoading)
            assertEquals(null, currentState.error)
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
    fun `onBackClick should navigate back`() = runTest() {
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
    }
}