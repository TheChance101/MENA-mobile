package net.thechance.mena.trends.presentation

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.presentation.screen.manage_my_trends.ManageTrendsViewModel
import net.thechance.mena.trends.utils.mockkReels
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull


class ManageMyTrendViewModelTest {

    private lateinit var repository: ReelsRepository
    private lateinit var viewModel: ManageTrendsViewModel

    @BeforeTest
    fun setUp() {

        repository = mock(mode = MockMode.autofill)
        viewModel = ManageTrendsViewModel(repository)
        everySuspend { repository.getAllReels(1) } returns mockkReels
    }

    @Test
    fun `init function should load reels`() = runTest{

        viewModel.state.test{
            val state = awaitItem()
            assertNotNull(state.reels)
        }
    }
}