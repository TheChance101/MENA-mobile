package net.thechance.mena.trends.presentation.screen.user_reel

import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
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
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.trends.domain.entity.Trend
import net.thechance.mena.trends.domain.model.TrendUrls
import net.thechance.mena.trends.domain.model.TrendWatchSession
import net.thechance.mena.trends.domain.repository.TrendsRepository
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.screen.user_trend.args.UserTrendArgs
import net.thechance.mena.trends.presentation.screen.user_trend.TrendWatchSessionState
import net.thechance.mena.trends.presentation.screen.user_trend.UserTrendEffect
import net.thechance.mena.trends.presentation.screen.user_trend.UserTrendViewModel
import net.thechance.mena.trends.presentation.screen.user_trend.toUserTrendUiState
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class UserTrendViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val mockTrendsRepository: TrendsRepository = mock(MockMode.autofill)
    private val userTrendArgs: UserTrendArgs = mock(MockMode.autofill) {
        every { trendId } returns "2"
    }

    private lateinit var viewModel: UserTrendViewModel


    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        everySuspend { mockTrendsRepository.getFeedTrends(any(), any()) } returns feedTrends
        every { userTrendArgs.trendSource } returns Route.TrendSource.Home
        viewModel = UserTrendViewModel(userTrendArgs, mockTrendsRepository, testDispatcher)
    }

    @Test
    fun `should initialize UserReelUiState with default state`() = runTest {
        viewModel.state.test {
            val initialState = awaitItem()

            assertThat(initialState.currentTrendId).isEqualTo("2")
            assertFalse(initialState.isLoading)
            assertNull(initialState.error)
            assertFalse(initialState.isConfirmationDialogVisible)
            assertNull(initialState.isTrendDeleted)
            assertFalse(initialState.isDescriptionExpanded)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `viewmodel should update state by reels when getFeedReels return data`() =
        runTest(testDispatcher) {
            everySuspend { mockTrendsRepository.getFeedTrends(any()) } returns feedTrends
            advanceUntilIdle()
            viewModel.state.test {
                val state = awaitItem()
                val reelsSnapshot = state.trends.asSnapshot()
                assertThat(reelsSnapshot).isEqualTo(expectedReelUiStateList)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `viewmodel init should update isLoading to false getFeedReels return data`() =
        runTest(testDispatcher) {
            everySuspend { mockTrendsRepository.getFeedTrends(any()) } returns feedTrends

            advanceUntilIdle()

            viewModel.state.test {
                assertThat(awaitItem().isLoading).isFalse()
            }
        }

    @Test
    fun `onClickDescription should expand description when called with collapsed state`() =
        runTest {
            viewModel.onClickDescription(isCollapsed = false)

            viewModel.state.test {
                val state = awaitItem()
                assertTrue(state.isDescriptionExpanded)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onClickDescription should collapse description when called with expanded state`() =
        runTest {
            viewModel.onClickDescription(isCollapsed = true)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.state.test {
                val state = awaitItem()
                assertFalse(state.isDescriptionExpanded)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onClickBack should send NavigateBack effect when called`() = runTest {
        viewModel.onClickBack()

        viewModel.effect.test {
            val effect = awaitItem()
            assertTrue(effect is UserTrendEffect.NavigateBack)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onChangeCurrentReel should update state with new reel id`() = runTest {
        viewModel.onChangeCurrentTrend("newReelId")

        viewModel.state.test {
            assertThat(awaitItem().currentTrendId).isEqualTo("newReelId")
        }
    }

    @Test
    fun `onClickDelete should show confirmation dialog when called`() = runTest {
        viewModel.onClickDelete()

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isConfirmationDialogVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDismissConfirmationDialog should dismiss confirmation dialog when called`() = runTest {
        viewModel.onClickDelete()

        viewModel.onDismissConfirmationDialog()
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isConfirmationDialogVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onClickConfirmDelete should delete reel successfully when is called`() = runTest {

        everySuspend { mockTrendsRepository.deleteTrendById("1") }


        viewModel.onClickConfirmDelete()

        viewModel.state.test {
            skipItems(1)

            val state2 = awaitItem()

            assertFalse(state2.isConfirmationDialogVisible)

            assertEquals(true, state2.isTrendDeleted)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDismissSuccessDialog should dismiss success dialog when called`() = runTest {
        viewModel.onDismissSuccessDialog()

        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.isTrendDeleted)
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
    fun `onClickConfirmDelete should update error state when repository throws exception`() = runTest(testDispatcher) {
        everySuspend { mockTrendsRepository.deleteTrendById(any()) } throws Exception("Failed")

        viewModel.onClickConfirmDelete()
        advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().error).isEqualTo(ErrorState.RequestFailed("Failed"))
        }
    }

    @Test
    fun `onClickPublisherInfo should send NavigateToPublisherProfile effect when called`() =
        runTest {
            viewModel.onClickPublisherInfo()

            viewModel.effect.test {
                val effect = awaitItem()
                assertTrue(effect is UserTrendEffect.NavigateToPublisherProfile)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `increaseReelView should call addReelView if isReelDeleted is null`() = runTest {
        viewModel.increaseTrendView("2")
        advanceUntilIdle()

        verifySuspend { mockTrendsRepository.addTrendView("2") }
    }

    @Test
    fun `should call saveUserEngagementWithReel in repository when saveUserReelEngagement called`() =
        runTest {
            viewModel.saveUserTrendEngagement(trendWatchSessionState, "1")
            advanceUntilIdle()

            verifySuspend { mockTrendsRepository.saveUserEngagementWithTrend(trendWatchSession) }
        }

    @Test
    fun `onRemoveReelLike method should remove like from Reel when called`() = runTest {
        everySuspend { mockTrendsRepository.removeTrendLike("2") } returns Unit
        advanceUntilIdle()

        val initial = viewModel.state.value.trends.asSnapshot().first { it.id == "2" }
        viewModel.onClickLike("2", true)
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            val updated = state.trends.asSnapshot().first { it.id == "2" }

            assertThat(updated.likesCount).isEqualTo(initial.likesCount - 1)
            assertThat(updated.isLiked).isFalse()

            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `onClickLike should call addReelLike method when isLiked is false`() = runTest {
        viewModel.onClickLike("1", false)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend { mockTrendsRepository.addTrendLike("1") }
    }

    @Test
    fun `onClickLike should call removeReelLike method when isLiked is true`() = runTest {
        viewModel.onClickLike("1", true)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend { mockTrendsRepository.removeTrendLike("1") }
    }

    @Test
    fun `updateReelInPagingData should only update the specific reel by id`() = runTest {
        everySuspend { mockTrendsRepository.addTrendLike("2") } returns trend2

        viewModel.onClickLike("2", true)
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            val updated = state.trends.asSnapshot().first { it.id == "2" }

            assertThat(updated.likesCount).isEqualTo(49)
            assertThat(updated.isLiked).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDismissErrorDialog should clear error and dismiss dialogs when called`() = runTest {
        viewModel.onDismissErrorDialog()

        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.error)
            assertNull(state.isTrendDeleted)
            assertFalse(state.isConfirmationDialogVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onGetRefreshVideoUrl should update the specific reel video url to new value by id`() = runTest {
        everySuspend { mockTrendsRepository.getTrendUrls("2") } returns TrendUrls(
            videoUrl = "video3.mp4",
            thumbnailUrl = "thumb1.jpg"
        )

        advanceUntilIdle()
        viewModel.onGetRefreshVideoUrl("2")
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            val reelsSnapshot = state.trends.asSnapshot().first()

            assertThat(reelsSnapshot.videoUrl).isEqualTo("video3.mp4")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onClickRetry should update currentReelId clear error and reload reels`() = runTest {
        viewModel.onNetworkError()
        advanceUntilIdle()

        viewModel.onClickRetry("123")
        advanceUntilIdle()

        verifySuspend { viewModel.onGetRefreshVideoUrl(any()) }
    }

    @Test
    fun `onNetworkError should update error state to NoInternet`() = runTest {
        viewModel.onNetworkError()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo(ErrorState.NoInternet)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }


    private companion object {
        val trend1 = Trend(
            id = "1",
            thumbnailUrl = "thumb1.jpg",
            videoUrl = "video1.mp4",
            description = "First reel",
            likesCount = 100,
            viewsCount = 1000,
            createdAt = LocalDateTime(2002, 2, 22, 2, 22),
            userName = "Nour",
            profileImageUrl = "",
            isCurrentUserOwner = true,
            isLiked = false
        )

        val trend2 = Trend(
            id = "2",
            thumbnailUrl = "thumb2.jpg",
            videoUrl = "video2.mp4",
            description = "second reel",
            likesCount = 50,
            viewsCount = 100,
            createdAt = LocalDateTime(2002, 2, 22, 2, 22),
            userName = "hend",
            profileImageUrl = "",
            isCurrentUserOwner = false,
            isLiked = true
        )

        val feedTrends = listOf(trend2, trend1)

        val expectedReelUiStateList = feedTrends.map { it.toUserTrendUiState() }

        val trendWatchSessionState = TrendWatchSessionState(
            trendId = "",
            watchStartTime = LocalDateTime(2024, 12, 10, 15, 30),
            watchEndTime = LocalDateTime(2024, 12, 10, 15, 31),
            videoDurationInMilliseconds = 60000,
            watchedDurationInMilliseconds = 60000
        )
        val trendWatchSession = TrendWatchSession(
            trendId = "1",
            watchStartTime = LocalDateTime(2024, 12, 10, 15, 30),
            watchEndTime = LocalDateTime(2024, 12, 10, 15, 31),
            videoDurationInMilliseconds = 60000,
            percentageOfVideoWatched = 100.0f
        )
    }
}