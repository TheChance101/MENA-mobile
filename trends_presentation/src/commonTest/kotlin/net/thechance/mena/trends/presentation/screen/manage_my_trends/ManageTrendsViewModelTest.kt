package net.thechance.mena.trends.presentation.screen.manage_my_trends

import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
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
import net.thechance.mena.trends.domain.entity.Trend
import net.thechance.mena.trends.domain.model.TrendUrls
import net.thechance.mena.trends.domain.repository.TrendsRepository
import net.thechance.mena.trends.presentation.navigation.Route
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalCoroutinesApi::class)
class ManageTrendsViewModelTest {
    private val repository: TrendsRepository = mock(MockMode.autofill)
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
    fun `view model should update state by reels when getAllReels returns data`() = runTest {
        everySuspend { repository.getAllCurrentUserTrends(any()) } returns reels

        viewModel.getTrends()
        advanceUntilIdle()

        val pagingData = viewModel.state.value.trends
        val reelsSnapshot: List<TrendUiState> = pagingData.asSnapshot()

        assertThat(reelsSnapshot).isEqualTo(expectedTrendUiStateLists)
    }

    @Test
    fun `onClickReel should navigate to trend screen with reel id`() = runTest(testDispatcher) {
        viewModel.effect.test {
            viewModel.onClickTrend(REEL_ID)
            assertThat(awaitItem())
                .isEqualTo(ManageTrendsUiEffect.NavigateToTrend(REEL_ID, TREND_SOURCE))
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
            verifySuspend { viewModel.getTrends() }
            verifySuspend { viewModel.getCurrentUserInfo() }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onGetRefreshedThumbnail updates the specific reel thumbnail by id`() = runTest {
        everySuspend { repository.getAllCurrentUserTrends(0) } returns reels
        everySuspend { repository.getTrendUrls(REEL_ID) } returns TrendUrls(
            videoUrl = "video3.mp4",
            thumbnailUrl = "thumb1.jpg"
        )

        viewModel.getTrends()
        advanceUntilIdle()

        viewModel.onGetRefreshedThumbnail(REEL_ID)
        advanceUntilIdle()

        val updatedReel = viewModel.state.value.trends
            .asSnapshot()
            .first { it.id == REEL_ID }

        assertThat(updatedReel.thumbnailUrl).isEqualTo("thumb1.jpg")
    }


    @Test
    fun `getFavoriteReels should update state with favorite reels`() = runTest {
        everySuspend { repository.getFavoriteTrends(pageNumber = 0) } returns reels

        viewModel.getFavoriteTrends()
        advanceUntilIdle()

        viewModel.state.test {
            val favoriteReelsFlow = viewModel.state.value.favoriteTrends!!
            val snapshot = favoriteReelsFlow.asSnapshot()
            assertThat(snapshot).isEqualTo(expectedTrendUiStateLists)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadSelectedTabData should update state with selected tab data`() = runTest {
        val selectedTab = SelectTab.MyTrends

        viewModel.loadSelectedTabData(selectedTab)

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.selectedTab).isEqualTo(selectedTab)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSelectTab should update state with selected tab`() = runTest {
        val selectedTab = SelectTab.Favorites

        viewModel.onSelectTab(selectedTab)
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.selectedTab).isEqualTo(selectedTab)
            cancelAndIgnoreRemainingEvents()
        }
    }

@Test
    fun `onClickReel should return Favorites Reels if selected tab is Favorites`() = runTest {
        viewModel.onSelectTab(SelectTab.Favorites)

        viewModel.onClickTrend(REEL_ID)

        viewModel.effect.test {
            assertThat(awaitItem())
                .isEqualTo(ManageTrendsUiEffect.NavigateToTrend(REEL_ID, Route.TrendSource.Favorites))
            cancelAndIgnoreRemainingEvents()
        }
    }

    private companion object {
        const val REEL_ID = "1"
        val TREND_SOURCE = Route.TrendSource.MyTrends
        val trend = Trend(
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
            trend,
            trend.copy(
                id = "2",
                thumbnailUrl = "thumb2.jpg",
                videoUrl = "video2.mp4",
                description = "Second reel",
            )
        )
        val expectedTrendUiStateLists = listOf(
            TrendUiState(id = "1", thumbnailUrl = "thumb1.jpg"),
            TrendUiState(id = "2", thumbnailUrl = "thumb2.jpg")
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