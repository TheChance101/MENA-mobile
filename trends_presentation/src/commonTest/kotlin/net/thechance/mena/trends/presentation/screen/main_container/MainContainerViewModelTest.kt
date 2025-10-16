package net.thechance.mena.trends.presentation.screen.main_container

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.trends.domain.repository.CategoryRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainContainerViewModelTest {
    private val repository: CategoryRepository = mock<CategoryRepository>()
    private lateinit var viewModel: MainContainerViewModel
    private val testDispatcher = StandardTestDispatcher()


    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        everySuspend { repository.isCategoriesAlreadySelectedByUser() } returns true
        viewModel = MainContainerViewModel(repository, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `navigateToUploadReel should navigate to upload reel screen`() = runTest {
        viewModel.navigateToUploadReel()
        viewModel.effect.test {
            assertThat(awaitItem()).isEqualTo(MainContainerEffect.NavigateToUploadReel)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `navigateToManageTrends should navigate to manage trends screen`() = runTest {
        viewModel.navigateToManageTrends()
        viewModel.effect.test {
            assertThat(awaitItem()).isEqualTo(MainContainerEffect.NavigateToManageTrends)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `navigateToCategories should navigate to categories screen when when user categories are not set`() =
        runTest {
            viewModel.handleGetIsUserCategorySet(isUserCategorySet = false)
            viewModel.effect.test {
                viewModel.navigateToCategories()
                assertThat(awaitItem()).isEqualTo(MainContainerEffect.NavigateToCategoryPick)
            }
        }

    @Test
    fun `navigateToCategories should navigate to trends screen when user categories are already set`() =
        runTest {
            viewModel.handleGetIsUserCategorySet(isUserCategorySet = true)
            viewModel.effect.test {
                viewModel.navigateToCategories()
                assertThat(awaitItem()).isEqualTo(MainContainerEffect.NavigateToTrends)
            }
        }

    @Test
    fun `handleGetIsUserCategorySet should update state with isUserCategorySet`() = runTest {
        viewModel.handleGetIsUserCategorySet(isUserCategorySet = true)
        assertThat(viewModel.state.value.isCategoriesAlreadySelectedByUser).isEqualTo(true)
    }

    @Test
    fun `loadCategories should update error state when repository throws exception`() = runTest {
        everySuspend { repository.isCategoriesAlreadySelectedByUser() } throws Exception()

        val viewModel = MainContainerViewModel(repository, testDispatcher)

        viewModel.state.test {
            skipItems(1)
            val state = awaitItem()
            assertThat(state.error).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
