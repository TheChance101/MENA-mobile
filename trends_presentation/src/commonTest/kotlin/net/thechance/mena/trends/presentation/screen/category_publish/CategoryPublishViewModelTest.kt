package net.thechance.mena.trends.presentation.screen.category_publish

import app.cash.turbine.test
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
import net.thechance.mena.trends.domain.entity.Category
import net.thechance.mena.trends.domain.repository.CategoryRepository
import net.thechance.mena.trends.domain.repository.ReelsRepository
import net.thechance.mena.trends.presentation.screen.category_publish.args.CategoryPublishArgs
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryPublishViewModelTest {

    private lateinit var viewModel: CategoryPublishViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val categoryPublishArgs = mock<CategoryPublishArgs>()
    private val categoryRepository = mock<CategoryRepository>()
    private val reelsRepository = mock<ReelsRepository>()

    private val sampleCategory = Category(id = "categoryId", name = "categoryName", emoji = "🔥")

    @BeforeTest
    fun setup() {
        every { categoryPublishArgs.trendId } returns "trendId"
        every { categoryPublishArgs.description } returns "description"
        everySuspend { categoryRepository.getAllCategories() } returns listOf(sampleCategory)
        Dispatchers.setMain(testDispatcher)
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
    fun `should navigate back when onBackClick is called`() = runTest {
        viewModel.onBackClick()
        viewModel.effect.test {
            val effect = awaitItem()
            assertTrue(effect is CategoryPublishEffect.NavigateBack)
        }
    }

    @Test
    fun `should navigate to trends when onPublishClick is called`() = runTest {
        viewModel.onPublishClick()

        viewModel.effect.test {
            val effect = awaitItem()
            assertTrue(effect is CategoryPublishEffect.NavigateToTrends)
        }
    }

    @Test
    fun `should update state with categories when loadCategories succeeds`() = runTest {
        viewModel.state.test {
            val initial = awaitItem()
            assertTrue(initial.categories.isNotEmpty())
            val category = initial.categories.first()
            assertEquals(sampleCategory.id, category.value.id)
        }
    }

    @Test
    fun `should call repository with correct params when publish is clicked`() = runTest {
        everySuspend {
            reelsRepository.updateReelById(any(), any(), any())
        } returns Unit
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onCategoryClick(sampleCategory.id)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onPublishClick()
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend {
            reelsRepository.updateReelById(
                id = "trendId",
                description = "description",
                categoryIds = listOf(sampleCategory.id)
            )
        }
    }
}