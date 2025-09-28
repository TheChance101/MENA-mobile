package net.thechance.mena.trends.presentation.screen.manage_my_trends

import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.trends.domain.entity.Category
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.domain.repository.ReelsRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith


@OptIn(ExperimentalCoroutinesApi::class)
class ManageTrendsViewModelTest {
    private val repository: ReelsRepository = mock(MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ManageTrendsViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ManageTrendsViewModel(repository, testDispatcher)
    }

    @Test
    fun `view model should update state by reels when getAllReels returns data`() =
        runTest(testDispatcher) {
            everySuspend { repository.getAllReels(1) } returns reelList

            viewModel.state.test {
                val currentState = awaitItem()
                val reelsSnapshot: List<ReelUiState> = currentState.reels.asSnapshot()
                assertThat(reelsSnapshot).isEqualTo(expectedReelUiStateList)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `initialize view model should handle error state when getAllReels fails`() =
        runTest(testDispatcher) {
            val errorMessage = "error"
            everySuspend { repository.getAllReels(1) } throws Exception(errorMessage)
            assertFailsWith<Exception> {
                viewModel.state.value.reels.asSnapshot()
            }
        }

    @Test
    fun `onReelItemClick should navigate to trend screen with reel id`() = runTest(testDispatcher) {
        viewModel.effect.test {
            viewModel.onReelItemClick(REEL_ID)
            assertThat(awaitItem()).isEqualTo(ManageTrendsUiEffect.NavigateToTrend(REEL_ID))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onBackClick should navigate back`() = runTest(testDispatcher) {
        viewModel.onBackClick()

        viewModel.effect.test {
            assertThat(awaitItem()).isEqualTo(ManageTrendsUiEffect.NavigateBack)
            cancelAndIgnoreRemainingEvents()
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