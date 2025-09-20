package net.thechance.mena.trends.presentation

import FakeLogger
import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.presentation.screen.manage_my_trends.ManageTrendsUiEffect
import net.thechance.mena.trends.presentation.screen.manage_my_trends.ManageTrendsViewModel
import net.thechance.mena.trends.presentation.screen.manage_my_trends.toUiState
import net.thechance.mena.trends.utils.FakeRepository
import net.thechance.mena.trends.utils.mockkReels
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


@OptIn(ExperimentalCoroutinesApi::class)

class ManageMyTrendViewModelTest {

    private lateinit var repository: ReelsRepository
    private lateinit var viewModel: ManageTrendsViewModel

    @BeforeTest
    fun setUp() {
        repository = FakeRepository()
        viewModel = ManageTrendsViewModel(repository, FakeLogger())
    }


    @Test
    fun `init function should load reels`() = runTest {

        viewModel.state.test {
            val state = awaitItem()

            val expectedReels = mockkReels.map { it.toUiState() }
            val actualReels = state.reels.asSnapshot()

            assertTrue(actualReels.containsAll(expectedReels))
        }
    }

    @Test
    fun `onReelItemClick should navigate to trend screen`() = runTest {
        val reelId = "1"
        viewModel.onReelItemClick(reelId)

        viewModel.effect.test {
            val effect = awaitItem()
            assertEquals(ManageTrendsUiEffect.NavigateToTrend(reelId), effect)
        }
    }

    @Test
    fun `onBackClick should navigate back`() = runTest {
        viewModel.onBackClick()
        
        viewModel.effect.test {

            val effect = awaitItem()
            assertEquals(ManageTrendsUiEffect.NavigateBack, effect)
        }
    }
}