package net.thechance.mena.trends.presentation.screen.show_real

import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
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
    private lateinit var viewModel: ReelViewModel

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
    )


    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ReelViewModel(repository, testDispatcher)
    }

    @Test
    fun `onVideoClick should send NavigateToReelDetails effect`() = runTest {
        viewModel.effect.test {
            viewModel.onReelClick("1")
            assertThat(awaitItem()).isEqualTo(ReelUiEffect.NavigateToReelDetails("1"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onAddReelClick should send NavigateToAddReel effect`() = runTest {
        viewModel.effect.test {
            viewModel.onAddReelClick()
            assertThat(awaitItem()).isEqualTo(ReelUiEffect.NavigateToAddReel)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onManageTrendsClick should send NavigateToManageTrends effect`() = runTest {
        viewModel.effect.test {
            viewModel.onManageMyTrendsClick()
            assertThat(awaitItem()).isEqualTo(ReelUiEffect.NavigateToManageMyTrends)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onEditTagsClick should send NavigateToChangeTags effect`() = runTest {
        viewModel.effect.test {
            viewModel.onEditTagsClick()
            assertThat(awaitItem()).isEqualTo(ReelUiEffect.NavigateToChangeTags)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getTrends should update state when success`() = runTest {
        everySuspend { repository.getFeedReels(1) } returns listOf(testReel)
        viewModel = ReelViewModel(repository, testDispatcher)
        advanceUntilIdle()
        val items = viewModel.state.value.reels?.asSnapshot()
        assertThat(items?.first()?.id).isEqualTo(testReel.id)
    }

    @Test
    fun `onLikeClick should increment likes count`() = runTest {
        everySuspend { repository.getFeedReels(1) } returns listOf(testReel)
        viewModel = ReelViewModel(repository, testDispatcher)
        advanceUntilIdle()
        val initial = viewModel.state.value.reels?.asSnapshot()?.first()
        viewModel.onLikeClick("1")
        val updated = viewModel.state.value.reels?.asSnapshot()?.first()
        assertThat(updated?.likes).isEqualTo(initial!!.likes + 1)
    }
}