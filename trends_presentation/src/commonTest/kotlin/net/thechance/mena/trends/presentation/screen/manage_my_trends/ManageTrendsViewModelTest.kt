package net.thechance.mena.trends.presentation.screen.manage_my_trends

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
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.trends.domain.entity.Category
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.domain.entity.User
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.domain.repository.UserRepository
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ManageTrendsViewModelTest {
    private val repository: ReelsRepository = mock(MockMode.autofill)
    private val userRepository: UserRepository = mock(MockMode.autofill)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ManageTrendsViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ManageTrendsViewModel(repository,userRepository ,testDispatcher)
        everySuspend { userRepository.getCurrentUserInfo() } returns user

    }


    @Test
    fun `getCurrentUserProfile should update state with profile when userRepository returns data`() =
        runTest(testDispatcher) {
            viewModel.getCurrentUserInfo()

            viewModel.state.test {
                skipItems(1)
                val successState = awaitItem()
                assertThat(successState.profile).isEqualTo(userInfoUiState)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `view model should update state by reels when getAllReels returns data`() =
        runTest(testDispatcher) {
            everySuspend { repository.getAllReels(1) } returns reelList

            viewModel.state.test {
                val currentState = awaitItem()
                val reelsSnapshot: List<ReelUiState> = currentState.reels.asSnapshot()
                assertThat(reelsSnapshot).isEqualTo(expectedReelUiStateList)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onReelItemClick should navigate to trend screen with reel id`() = runTest(testDispatcher) {
        viewModel.effect.test {
            viewModel.onReelItemClick(REEL_ID)
            assertThat(awaitItem()).isEqualTo(ManageTrendsUiEffect.NavigateToTrend(REEL_ID))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onBackClick should navigate back`() = runTest(testDispatcher) {
        viewModel.onBackClick()

        viewModel.effect.test {
            assertThat(awaitItem()).isEqualTo(ManageTrendsUiEffect.NavigateBack)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private companion object {
        const val REEL_ID = "1"
        val reelList = listOf(
            Reel(
                id = "1",
                thumbnailUrl = "thumb1.jpg",
                videoUrl = "video1.mp4",
                description = "First reel",
                likesCount = 100,
                viewsCount = 1000,
                createdAt = LocalDateTime(2023, 10, 1, 12, 0),
                userName = "Alice",
                profileImageUrl = "https://example.com/alice.jpg",
                isCurrentUserOwner = false,
                categories = listOf(Category("1", "Trend", ":fire:"))
            ),
            Reel(
                id = "2",
                thumbnailUrl = "thumb2.jpg",
                videoUrl = "video2.mp4",
                description = "Second reel",
                likesCount = 200,
                viewsCount = 2000,
                createdAt = LocalDateTime(2023, 10, 2, 12, 0),
                userName = "Bob",
                profileImageUrl = "https://example.com/bob.jpg",
                isCurrentUserOwner = true,
                categories = listOf(Category("2", "Viral", ":rocket:"))
            )
        )
        val expectedReelUiStateList = listOf(
            ReelUiState(
                id = "1",
                thumbnailUrl = "thumb1.jpg",
            ),
            ReelUiState(
                id = "2",
                thumbnailUrl = "thumb2.jpg",
            )
        )
        val user = User(
            username = "nour",
            firstName = "nour",
            lastName = "nour",
            profileImageUrl = "img.jpg"
        )
        val userInfoUiState = UserInfoUiState(
            userName = "nour",
            profileImageUrl = "img.jpg"
        )
    }
}