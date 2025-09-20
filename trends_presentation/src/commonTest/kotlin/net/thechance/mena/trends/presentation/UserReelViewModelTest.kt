package net.thechance.mena.trends.presentation

import FakeLogger
import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.presentation.screen.user_reel.UserReelEffect
import net.thechance.mena.trends.presentation.screen.user_reel.UserReelViewModel
import net.thechance.mena.trends.presentation.shared.util.StateHandle
import net.thechance.mena.trends.utils.FakeRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class UserReelViewModelTest {

    private lateinit var reelsRepository: ReelsRepository
    private lateinit var savedStateHandle: StateHandle
    private val viewModel: UserReelViewModel by lazy {
        UserReelViewModel (savedStateHandle, FakeLogger(),reelsRepository)
    }


    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())

        reelsRepository = FakeRepository()
        savedStateHandle = mock<StateHandle>()
    }

    @Test
    fun `when click to delete should show confirmation dialog`() = runTest {
        viewModel.onDeleteClick()

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isConfirmationDialogVisible)
        }
    }

    @Test
    fun `when click to confirm delete should delete reel`() = runTest {
        viewModel.onConfirmDeleteClick()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isConfirmationDialogVisible)
            assertTrue(state.isReelDeleted == true)

        }
    }
    @Test
    fun `when click to dismiss success dialog should hide confirmation dialog`() = runTest {
        viewModel.onDismissSuccessDialog()
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isConfirmationDialogVisible)
            assertTrue(state.isReelDeleted == null)
        }
    }

    @Test
    fun `when click to dismiss confirmation dialog should hide confirmation dialog`() = runTest {
        viewModel.onDismissConfirmationDialog()
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isConfirmationDialogVisible)
        }
    }

    @Test
    fun `when click to dismiss error dialog should hide confirmation dialog`() = runTest {
        viewModel.onDismissErrorDialog()
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isConfirmationDialogVisible)
            assertTrue(state.isReelDeleted == null)
        }
    }

    @Test
    fun `when click to back button should navigate back`() = runTest {
        viewModel.onBackClick()

        viewModel.effect.test {
            val effect = awaitItem()
            assertTrue(effect is UserReelEffect.NavigateBack)
        }
    }

    @Test
    fun `when click to description should expand description`() = runTest {
        viewModel.onDescriptionClick(false)

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isDescriptionExpanded)
        }
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

}