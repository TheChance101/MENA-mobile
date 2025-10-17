package net.thechance.mena.trends.presentation.screen.home

import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.domain.repository.ReelsRepository
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReelViewModelTest {

    private val repository: ReelsRepository = mock(MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel(repository, testDispatcher)
    }

    @Test
    fun `onVideoClick should send NavigateToReelDetails effect`() = runTest {
        viewModel.effect.test {
            viewModel.onReelClick("1")
            assertThat(awaitItem()).isEqualTo(HomeUiEffect.NavigateToReelDetails("1"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onAddReelClick should send NavigateToAddReel effect`() = runTest {
        viewModel.effect.test {
            viewModel.onAddReelClick()
            assertThat(awaitItem()).isEqualTo(HomeUiEffect.NavigateToAddReel)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onManageTrendsClick should send NavigateToManageTrends effect`() = runTest {
        viewModel.effect.test {
            viewModel.onManageMyTrendsClick()
            assertThat(awaitItem()).isEqualTo(HomeUiEffect.NavigateToManageMyTrends)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onEditTagsClick should send NavigateToChangeTags effect`() = runTest {
        viewModel.effect.test {
            viewModel.onEditTagsClick()
            assertThat(awaitItem()).isEqualTo(HomeUiEffect.NavigateToChangeTags)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getTrends should update state when success`() = runTest {
        everySuspend { repository.getFeedReels(1) } returns listOf(testReel)
        viewModel = HomeViewModel(repository, testDispatcher)
        advanceUntilIdle()
        val items = viewModel.state.value.reels?.asSnapshot()
        assertThat(items?.first()?.id).isEqualTo(testReel.id)
    }

    @Test
    fun `onLikeClick should increment likes count`() = runTest {
        everySuspend { repository.getFeedReels(1) } returns listOf(testReel)
        everySuspend { repository.toggleReelLike("1") } returns testReel.copy(
            isLiked = true,
            likesCount = testReel.likesCount + 1
        )

        viewModel = HomeViewModel(repository, testDispatcher)
        advanceUntilIdle()

        val initial = viewModel.state.value.reels.asSnapshot().first()

        viewModel.state.test {

            viewModel.onLikeClick("1")
            advanceUntilIdle()

            val state = awaitItem()
            val updated = state.reels.asSnapshot().first()

            assertThat(updated.likesCount).isEqualTo(initial.likesCount + 1)
            assertThat(updated.isLiked).isTrue()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onLikeClick should revert optimistic update when toggleReelLike fails`() = runTest {
        everySuspend { repository.getFeedReels(1) } returns listOf(testReel)
        everySuspend { repository.toggleReelLike("1") } throws Exception("Network error")

        advanceUntilIdle()
        val initial = viewModel.state.value.reels.asSnapshot().first()

        viewModel.onLikeClick("1")
        advanceUntilIdle()

        val reverted = viewModel.state.value.reels.asSnapshot().first()

        assertThat(reverted.likesCount).isEqualTo(initial.likesCount)
        assertThat(reverted.isLiked).isEqualTo(initial.isLiked)

        assertThat(viewModel.state.value.error != null).isTrue()
    }

    companion object {
        private val testReel = Reel(
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
        )
    }
}