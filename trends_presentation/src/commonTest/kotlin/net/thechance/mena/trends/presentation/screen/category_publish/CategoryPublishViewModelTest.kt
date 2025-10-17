package net.thechance.mena.trends.presentation.screen.category_publish

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isNotNull
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
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.trends.domain.repository.CategoryRepository
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.presentation.screen.category_publish.args.CategoryPublishArgs
import net.thechance.mena.trends.presentation.utils.categories
import net.thechance.mena.trends.presentation.utils.category
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryPublishViewModelTest {
    private lateinit var viewModel: CategoryPublishViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val categoryPublishArgs = mock<CategoryPublishArgs>()
    private val categoryRepository = mock<CategoryRepository>()
    private val reelsRepository = mock<ReelsRepository>()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        everySuspend { categoryRepository.getAllCategories() } returns categories
        every { categoryPublishArgs.trendId } returns TREND_ID
        every { categoryPublishArgs.description } returns DESCRIPTION
        everySuspend {
            reelsRepository.updateReelById(any(), any(), any())
        } returns Unit

        viewModel = CategoryPublishViewModel(
            categoryPublishArgs = categoryPublishArgs,
            categoryRepository = categoryRepository,
            reelsRepository = reelsRepository,
            defaultDispatcher = testDispatcher
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onBackClick should emit NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onBackClick()
            val effect = awaitItem()
            assertTrue(effect is CategoryPublishEffect.NavigateBack)
        }
    }

    @Test
    fun `onPublishClick should emit NavigateToTrends effect`() = runTest {
        viewModel.effect.test {
            viewModel.onPublishClick()
            val effect = awaitItem()
            assertTrue(effect is CategoryPublishEffect.NavigateToHome)
        }
    }

    @Test
    fun `loadCategories should update state with categories when succeeds`() = runTest {
        viewModel.state.test {
            val initial = awaitItem()
            assertTrue(initial.categories.isNotEmpty())
            val categoryState = initial.categories.first()
            assertEquals(category.id, categoryState.value.id)
        }
    }

    @Test
    fun `onPublishClick should update error state when updateReelById throws exception`() =
        runTest {
            everySuspend {
                reelsRepository.updateReelById(any(), any(), any())
            } throws Exception()

            viewModel.onCategoryClick(category.id)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onPublishClick()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.state.test {
                val state = awaitItem()
                assertThat(state.error).isNotNull()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadCategories should update error state when repository throws exception`() = runTest {
        everySuspend { categoryRepository.getAllCategories() } throws Exception()

        val viewModel = CategoryPublishViewModel(
            categoryPublishArgs = categoryPublishArgs,
            categoryRepository = categoryRepository,
            reelsRepository = reelsRepository,
            defaultDispatcher = testDispatcher
        )

        viewModel.state.test {
            skipItems(1)
            val state = awaitItem()
            assertThat(state.error).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onPublishClick should call updateReelById with correct params when category is selected`() =
        runTest {
            viewModel.onCategoryClick(category.id)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onPublishClick()
            testDispatcher.scheduler.advanceUntilIdle()

            verifySuspend {
                reelsRepository.updateReelById(
                    id = TREND_ID,
                    description = DESCRIPTION,
                    categoryIds = listOf(category.id)
                )
            }
        }

    private companion object {
        const val TREND_ID = "trendId"
        const val DESCRIPTION = "description"
    }
}