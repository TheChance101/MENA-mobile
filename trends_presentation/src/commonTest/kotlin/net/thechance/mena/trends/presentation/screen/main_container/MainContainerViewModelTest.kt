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
    fun `handleGetIsUserCategorySet should update state with isUserCategorySet`() = runTest {
        viewModel.onUserCategoryStatusReceived(isUserCategorySet = true)
        assertThat(viewModel.state.value.isCategoriesAlreadySelectedByUser).isEqualTo(true)
    }

    @Test
    fun `handleGetIsUserCategorySet should navigate to home screen when isUserCategorySet is true`() =
        runTest {
            viewModel.onUserCategoryStatusReceived(isUserCategorySet = true)
            viewModel.effect.test {
                val effect = awaitItem()
                assertThat(effect).isEqualTo(MainContainerEffect.NavigateToTrendHome)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleGetIsUserCategorySet should navigate to category pick screen when isUserCategorySet is false`() =
        runTest {
            val viewModel =
                MainContainerViewModel(repository = repository, defaultDispatcher = testDispatcher)
            viewModel.effect.test {
                viewModel.onUserCategoryStatusReceived(false)
                assertThat(awaitItem()).isEqualTo(MainContainerEffect.NavigateToCategoryPick)
                cancelAndIgnoreRemainingEvents()
            }
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