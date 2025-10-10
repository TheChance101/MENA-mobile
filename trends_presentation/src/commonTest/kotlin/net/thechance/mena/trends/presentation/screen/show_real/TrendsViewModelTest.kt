package net.thechance.mena.trends.presentation.screen.show_real

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
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
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.domain.repository.ReelsRepository
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TrendsViewModelTest {

    private val repository: ReelsRepository = mock(MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: TrendsViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TrendsViewModel(repository, testDispatcher)
    }

    @Test
    fun `onVideoClick should send NavigateToReelDetails effect`() = runTest {
        viewModel.effect.test {
            viewModel.onReelClick("1")
            assertThat(awaitItem()).isEqualTo(TrendsUiEffect.NavigateToReelDetails("1"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onAddReelClick should send NavigateToAddReel effect`() = runTest {
        viewModel.effect.test {
            viewModel.onAddReelClick()
            assertThat(awaitItem()).isEqualTo(TrendsUiEffect.NavigateToAddReel)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onManageTrendsClick should send NavigateToManageTrends effect`() = runTest {
        viewModel.effect.test {
            viewModel.onManageMyTrendsClick()
            assertThat(awaitItem()).isEqualTo(TrendsUiEffect.NavigateToManageMyTrends)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onEditTagsClick should send NavigateToChangeTags effect`() = runTest {
        viewModel.effect.test {
            viewModel.onEditTagsClick()
            assertThat(awaitItem()).isEqualTo(TrendsUiEffect.NavigateToChangeTags)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getTrends should toggle loading state`() = runTest(testDispatcher) {
        everySuspend { repository.getFeedReels(1) } returns mockReels

        viewModel.state.test {
            val states = mutableListOf<TrendsScreenState>()
            repeat(2) { states += awaitItem() }

            assertThat(states[0].isLoading).isEqualTo(true)
            assertThat(states[1].isLoading).isEqualTo(false)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private companion object {
        val mockReels = listOf(
            Reel("1", "thumb1.jpg", "video1.mp4", "desc1", 10, 100, null, emptyList()),
            Reel("2", "thumb2.jpg", "video2.mp4", "desc2", 20, 200, null, emptyList())
        )

        val mockReelsUi = listOf(
            TrendUiState("1", "thumb1.jpg", "video1.mp4", "desc1", likes = 10, views = 100),
            TrendUiState("2", "thumb2.jpg", "video2.mp4", "desc2", likes = 20, views = 200)
        )
    }
}