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
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
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
class HomeViewModelTest {

    private val repository: ReelsRepository = mock(MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        everySuspend { repository.getFeedReels(any()) } returns reels
        viewModel = HomeViewModel(repository, testDispatcher)
    }

    @Test
    fun `onClickReel should send NavigateToReelDetails effect`() = runTest {
        viewModel.effect.test {
            viewModel.onClickReel("1")
            assertThat(awaitItem()).isEqualTo(HomeUiEffect.NavigateToReelDetails("1"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onClickAddReel should send NavigateToAddReel effect`() = runTest {
        viewModel.effect.test {
            viewModel.onClickAddReel()
            assertThat(awaitItem()).isEqualTo(HomeUiEffect.NavigateToAddReel)
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
        val items = viewModel.state.value.reels.asSnapshot()
        assertThat(items.first().id).isEqualTo(reels[0].id)
    }

    @Test
    fun `onAddReelLike method should add like to Reel when called`() = runTest {
        everySuspend { repository.addReelLike("1") } returns reels[0].copy(
            isLiked = true,
            likesCount = reels[0].likesCount + 1
        )

        viewModel = HomeViewModel(repository, testDispatcher)
        advanceUntilIdle()

        val initial = viewModel.state.value.reels.asSnapshot().first()
        viewModel.addReelLike("1")
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            val updated = state.reels.asSnapshot().first()

            assertThat(updated.likesCount).isEqualTo(initial.likesCount + 1)
            assertThat(updated.isLiked).isTrue()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onRemoveReelLike method should remove like from Reel when called`() = runTest {
        everySuspend { repository.removeReelLike("2") } returns Unit
        advanceUntilIdle()

        val initial = viewModel.state.value.reels.asSnapshot().first { it.id == "2" }
        viewModel.removeReelLike("2")
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            val updated = state.reels.asSnapshot().first { it.id == "2" }

            assertThat(updated.likesCount).isEqualTo(initial.likesCount - 1)
            assertThat(updated.isLiked).isFalse()

            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `onClickLike should call addReelLike method when isLiked is false`() = runTest {
        viewModel.onClickLike("1", false)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend { repository.addReelLike("1") }
    }

    @Test
    fun `onClickLike should call removeReelLike method when isLiked is true`() = runTest {
        viewModel.onClickLike("1", true)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend { repository.removeReelLike("1") }
    }

    @Test
    fun `onRetryClick should reset error and call getFeedReels`() = runTest {
        viewModel.onClickRetry()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.error).isNull()
            verifySuspend { viewModel.getFeedReels() }
            cancelAndIgnoreRemainingEvents()
        }
    }

    companion object {

        private val reels = listOf(
            Reel(
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
            Reel(
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
    }
}