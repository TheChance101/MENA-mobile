package net.thechance.mena.trends.presentation.screen.manage_my_trends

import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.User
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.domain.model.ReelUrls
import net.thechance.mena.trends.domain.repository.ReelsRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalCoroutinesApi::class)
class ManageTrendsViewModelTest {
    private val repository: ReelsRepository = mock(MockMode.autofill)
    private val userRepository: UserRepository = mock(MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ManageTrendsViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ManageTrendsViewModel(repository, userRepository, testDispatcher)
        everySuspend { userRepository.getUser() } returns flowOf(user)
    }


    @Test
    fun `getCurrentUserProfile updates state with profile`() = runTest {
        viewModel.state.test {
            awaitItem()
            val updatedState = awaitItem()

            assertThat(updatedState.profile).isEqualTo(userInfoUiState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `view model should update state by reels when getAllReels returns data`() =
        runTest(testDispatcher) {
            everySuspend { repository.getAllCurrentUserReels(any()) } returns reels

            viewModel.state.test {
                val currentState = awaitItem()
                val reelsSnapshot: List<ReelUiState> = currentState.reels.asSnapshot()
                assertThat(reelsSnapshot).isEqualTo(expectedReelUiStateList)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `initialize view model should handle error state when getAllReels fails`() =
        runTest(testDispatcher) {
            val errorMessage = "error"
            everySuspend { repository.getAllCurrentUserReels(1) } throws Exception(errorMessage)
            assertFailsWith<Exception> {
                viewModel.state.value.reels.asSnapshot()
            }
        }

    @Test
    fun `onClickReel should navigate to trend screen with reel id`() = runTest(testDispatcher) {
        viewModel.effect.test {
            viewModel.onClickReel(REEL_ID)
            assertThat(awaitItem()).isEqualTo(ManageTrendsUiEffect.NavigateToTrend(REEL_ID))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onClickBack should navigate back`() = runTest(testDispatcher) {
        viewModel.onClickBack()

        viewModel.effect.test {
            assertThat(awaitItem()).isEqualTo(ManageTrendsUiEffect.NavigateBack)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onRetryClick should reset error and call getReels and getCurrentUserInfo`() = runTest {
        viewModel.onClickRetry()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.error).isNull()
            verifySuspend { viewModel.getReels() }
            verifySuspend { viewModel.getCurrentUserInfo() }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onGetRefreshVideoUrl should update the specific reel video url to new value by id`() = runTest {
        everySuspend { repository.getAllCurrentUserReels(0) } returns reels

        everySuspend { repository.getReelUrls(REEL_ID) } returns ReelUrls(
            videoUrl = "video3.mp4",
            thumbnailUrl = "thumb3.jpg"
        )

        advanceUntilIdle()

        viewModel.state.test {
            skipItems(1)

            viewModel.onGetRefreshedThumbnail(REEL_ID)

            val state = awaitItem()
            val reelsSnapshot = state.reels.asSnapshot().first()

            assertThat(reelsSnapshot.thumbnailUrl).isEqualTo("thumb3.jpg")
            cancelAndIgnoreRemainingEvents()
        }
    }

    private companion object {
        const val REEL_ID = "1"
        val reel = Reel(
            id = "1",
            thumbnailUrl = "thumb1.jpg",
            videoUrl = "video1.mp4",
            description = "First reel",
            likesCount = 100,
            viewsCount = 1000,
            createdAt = LocalDateTime(2002, 2, 22, 2, 22),
            userName = "mTm",
            profileImageUrl = "",
            isCurrentUserOwner = true,
            isLiked = false
        )

        val reels = listOf(
            reel,
            reel.copy(
                id = "2",
                thumbnailUrl = "thumb2.jpg",
                videoUrl = "video2.mp4",
                description = "Second reel",
            )
        )
        val expectedReelUiStateList = listOf(
            ReelUiState(id = "1", thumbnailUrl = "thumb1.jpg"),
            ReelUiState(id = "2", thumbnailUrl = "thumb2.jpg")
        )

        @OptIn(ExperimentalUuidApi::class)
        val user = User(
            id = Uuid.random(),
            firstName = "nour",
            lastName = "nour",
            profileImageUrl = "img.jpg",
            username = "nour",
            birthDate = LocalDate(2000, 1, 1),
            gender = Gender.FEMALE
        )
        val userInfoUiState = UserInfoUiState(
            userName = "nour",
            profileImageUrl = "img.jpg"
        )
    }
}