package net.thechance.mena.trends.presentation.screen.user_reel

import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
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
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.presentation.screen.user_reel.args.UserReelArgs
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class UserReelViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val mockReelsRepository: ReelsRepository = mock(MockMode.autofill)
    private val userReelArgs: UserReelArgs = mock(MockMode.autofill) {
        every { realId } returns "2"
    }

    private lateinit var viewModel: UserReelViewModel


    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        everySuspend { mockReelsRepository.getFeedReels(any(), any()) } returns feedReels
        viewModel = UserReelViewModel(userReelArgs, mockReelsRepository, testDispatcher)
    }

    @Test
    fun `should initialize UserReelUiState with default state`() = runTest {
        viewModel.state.test {
            skipItems(1)
            val initialState = awaitItem()

            assertFalse(initialState.isLoading)
            assertNull(initialState.error)
            assertFalse(initialState.isConfirmationDialogVisible)
            assertNull(initialState.isReelDeleted)
            assertFalse(initialState.isDescriptionExpanded)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `viewmodel should update state by reels when getFeedReels return data`() =
        runTest(testDispatcher) {
            everySuspend { mockReelsRepository.getFeedReels(any()) } returns feedReels
            advanceUntilIdle()
            viewModel.state.test {
                val state = awaitItem()
                val reelsSnapshot = state.reels.asSnapshot()
                assertThat(reelsSnapshot).isEqualTo(expectedReelUiStateList)
                cancelAndIgnoreRemainingEvents()
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
            assertTrue(effect is UserReelEffect.NavigateBack)
            cancelAndIgnoreRemainingEvents()
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

        everySuspend { mockReelsRepository.deleteReelById("1") }


        viewModel.onClickConfirmDelete()

        viewModel.state.test {
            skipItems(1)

            val state2 = awaitItem()

            assertFalse(state2.isConfirmationDialogVisible)

            assertEquals(true, state2.isReelDeleted)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDismissSuccessDialog should dismiss success dialog when called`() = runTest {
        viewModel.onDismissSuccessDialog()

        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.isReelDeleted)
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
    fun `onClickConfirmDelete should update error state when repository throws exception`() =
        runTest {

            val errorMockRepository: ReelsRepository = mock(MockMode.autofill) {
                everySuspend { deleteReelById("1") } throws Exception()
            }

            val errorViewModel =
                UserReelViewModel(userReelArgs, errorMockRepository, testDispatcher)

            errorViewModel.onClickConfirmDelete()
            testDispatcher.scheduler.advanceUntilIdle()

            errorViewModel.state.test {
                val errorState = awaitItem()
                assertNotNull(errorState.error is ErrorState)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onClickPublisherInfo should send NavigateToPublisherProfile effect when called`() =
        runTest {
            viewModel.onClickPublisherInfo()

            viewModel.effect.test {
                val effect = awaitItem()
                assertTrue(effect is UserReelEffect.NavigateToPublisherProfile)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `increaseReelView should update error state when repository throws exception`() = runTest {
        everySuspend { mockReelsRepository.addReelView(any()) } throws Exception("View failed")

        viewModel.increaseReelView("2")
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertNotNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onAddReelLike method should add like to Reel when called`() = runTest {
        everySuspend { mockReelsRepository.addReelLike("2") } returns feedReels[0].copy(
            isLiked = true,
            likesCount = feedReels[0].likesCount + 1
        )

        advanceUntilIdle()

        val initial = viewModel.state.value.reels.asSnapshot().first()
        viewModel.addReelLike("2")
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
        everySuspend { mockReelsRepository.removeReelLike("2") } returns Unit
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

        verifySuspend { mockReelsRepository.addReelLike("1") }
    }

    @Test
    fun `onClickLike should call removeReelLike method when isLiked is true`() = runTest {
        viewModel.onClickLike("1", true)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend { mockReelsRepository.removeReelLike("1") }
    }

    @Test
    fun `updateReelInPagingData should only update the specific reel by id`() = runTest {
        everySuspend { mockReelsRepository.addReelLike("2") } returns reel2.copy(
            isLiked = true,
            likesCount = 51
        )

        advanceUntilIdle()
        viewModel.onClickLike("2", false)
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            val reelsSnapshot = state.reels.asSnapshot().first()

            assertThat(reelsSnapshot.likesCount).isEqualTo(51)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDismissErrorDialog should clear error and dismiss dialogs when called`() = runTest {
        viewModel.onDismissErrorDialog()

        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.error)
            assertNull(state.isReelDeleted)
            assertFalse(state.isConfirmationDialogVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }


    private companion object {
        val reel1 = Reel(
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

        val reel2 = Reel(
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

        val feedReels = listOf(reel2, reel1)

        val expectedReelUiStateList = feedReels.map { it.toUserReelUiState() }
    }
}