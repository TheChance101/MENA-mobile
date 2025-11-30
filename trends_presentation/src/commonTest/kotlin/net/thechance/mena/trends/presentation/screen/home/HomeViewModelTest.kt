package net.thechance.mena.trends.presentation.screen.home

import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.trends.domain.entity.Trend
import net.thechance.mena.trends.domain.model.TrendUpdates
import net.thechance.mena.trends.domain.model.TrendUrls
import net.thechance.mena.trends.domain.repository.TrendsRepository
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val repository: TrendsRepository = mock(MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        everySuspend { repository.getFeedTrends(any()) } returns trends
        viewModel = HomeViewModel(repository, testDispatcher)
    }

    @Test
    fun `onClickReel should send NavigateToReelDetails effect`() = runTest {
        viewModel.effect.test {
            viewModel.onClickTrend("1")
            assertThat(awaitItem()).isEqualTo(HomeUiEffect.NavigateToTrendDetails("1"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onClickAddReel should send NavigateToAddReel effect`() = runTest {
        viewModel.effect.test {
            viewModel.onClickAddTrend()
            assertThat(awaitItem()).isEqualTo(HomeUiEffect.NavigateToAddTrend)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onClickManageMyTrends should send NavigateToManageTrends effect`() = runTest {
        viewModel.effect.test {
            viewModel.onClickManageMyTrends()
            assertThat(awaitItem()).isEqualTo(HomeUiEffect.NavigateToManageMyTrends)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onClickEditTags should send NavigateToChangeTags effect`() = runTest {
        viewModel.effect.test {
            viewModel.onClickEditTags()
            assertThat(awaitItem()).isEqualTo(HomeUiEffect.NavigateToChangeTags)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getTrends should update state when success`() = runTest {
        viewModel = HomeViewModel(repository, testDispatcher)
        advanceUntilIdle()
        val items = viewModel.state.value.trends.asSnapshot()
        assertThat(items.first().id).isEqualTo(trends[0].id)
    }

    @Test
    fun `onAddReelLike method should add like to Reel when called`() = runTest {
        everySuspend { repository.addTrendLike("1") } returns trends[0].copy(
            isLiked = true,
            likesCount = trends[0].likesCount + 1
        )

        viewModel = HomeViewModel(repository, testDispatcher)
        advanceUntilIdle()

        val initial = viewModel.state.value.trends.asSnapshot().first()
        viewModel.onClickLike("1", true)
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            val updated = state.trends.asSnapshot().first()

            assertThat(updated.likesCount).isEqualTo(initial.likesCount + 1)
            assertThat(updated.isLiked).isTrue()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onRemoveReelLike method should remove like from Reel when called`() = runTest {
        everySuspend { repository.removeTrendLike("2") } returns Unit
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

        verifySuspend { repository.addTrendLike("1") }
    }

    @Test
    fun `onClickLike should call removeReelLike method when isLiked is true`() = runTest {
        viewModel.onClickLike("1", true)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend { repository.removeTrendLike("1") }
    }

    @Test
    fun `onRetryClick should reset error and call getFeedReels`() = runTest {
        viewModel.onClickRetry()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.error).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }
    @Test
    fun `onClickExpandDescription should toggle isDescriptionExpanded for specific reel`() = runTest {
        viewModel = HomeViewModel(repository, testDispatcher)
        advanceUntilIdle()

        val initial = viewModel.state.value.trends.asSnapshot().first { it.id == "1" }
        viewModel.onClickExpandDescription("1")
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            val updated = state.trends.asSnapshot().first { it.id == "1" }

            assertThat(updated.isDescriptionExpanded).isEqualTo(!initial.isDescriptionExpanded)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onGetRefreshVideoUrl should update the specific reel video url to new value by id`() = runTest {
        everySuspend { repository.getTrendUrls("1") } returns TrendUrls(
            videoUrl = "video3.mp4",
            thumbnailUrl = "thumb1.jpg"
        )

        advanceUntilIdle()
        viewModel.onGetRefreshedThumbnail("1")
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            val reelsSnapshot = state.trends.asSnapshot().first()

            assertThat(reelsSnapshot.thumbnailUrl).isEqualTo("thumb1.jpg")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeTrendsUpdates should call repository observeTrendUpdates`() = runTest {
        val updatesFlow = MutableSharedFlow<TrendUpdates>()
        every { repository.observeTrendUpdates() } returns updatesFlow

        viewModel = HomeViewModel(repository, testDispatcher)
        advanceUntilIdle()

        verifySuspend { repository.observeTrendUpdates() }
    }

    @Test
    fun `observeTrendsUpdates should update trend properties when isDeleted is false`() = runTest(testDispatcher) {
        val updatesFlow = MutableSharedFlow<TrendUpdates>()
        everySuspend { repository.getFeedTrends(page = 1) } returns trends
        every { repository.observeTrendUpdates() } returns updatesFlow

        viewModel = HomeViewModel(repository, testDispatcher)
        advanceUntilIdle()

        updatesFlow.emit(trendUpdates)
        advanceUntilIdle()

        viewModel.state.test {
            val snapshot = awaitItem().trends.asSnapshot()
            val updatedTrend = snapshot.first { it.id == trendUpdates.trendId }

            assertThat(updatedTrend.isLiked).isEqualTo(trendUpdates.isLiked)
            assertThat(updatedTrend.likesCount).isEqualTo(trendUpdates.likesCount)
            assertThat(updatedTrend.viewsCount).isEqualTo(trendUpdates.viewsCount)
        }
    }

    companion object {

        private val trends = listOf(
            Trend(
                id = "1",
                thumbnailUrl = "thumb.jpg",
                videoUrl = "video.mp4",
                description = "description",
                likesCount = 5,
                viewsCount = 50,
                createdAt = null,
                userName = "Test User",
                profileImageUrl = "https://example.com/profile.jpg",
                isCurrentUserOwner = false,
                isLiked = false
            ),
            Trend(
                id = "2",
                thumbnailUrl = "thumb.jpg",
                videoUrl = "video.mp4",
                description = "description",
                likesCount = 5,
                viewsCount = 50,
                createdAt = null,
                userName = "Test User",
                profileImageUrl = "https://example.com/profile.jpg",
                isCurrentUserOwner = false,
                isLiked = true
            )
        )

        val trendUpdates = TrendUpdates(
            trendId = "1",
            isLiked = false,
            likesCount = 6,
            viewsCount = 51,
            isDeleted = false
        )
    }
}