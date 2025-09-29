package net.thechance.mena.trends.presentation.screen.video_description

import app.cash.turbine.test
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.trends.presentation.screen.video_description.args.VideoDescriptionArgs
import net.thechance.mena.trends.presentation.utils.largeNewDescription
import net.thechance.mena.trends.presentation.utils.smallNewDescription
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class VideoDescriptionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: VideoDescriptionViewModel
    private val videoDescriptionArgs = mock<VideoDescriptionArgs>()

    @BeforeTest
    fun setup() {
        every { videoDescriptionArgs.trendId } returns "trendId"
        Dispatchers.setMain(testDispatcher)
        viewModel = VideoDescriptionViewModel(videoDescriptionArgs)
    }

    @Test
    fun `should update description when onDescriptionChanged is called`() = runTest {
        viewModel.onDescriptionChanged(smallNewDescription)

        viewModel.state.test {
            val initialState = awaitItem()
            assertTrue(initialState.description == smallNewDescription)
        }
    }

    @Test
    fun `should update currentNumberOfCharacters when onDescriptionChanged is called`() = runTest {
        viewModel.onDescriptionChanged(smallNewDescription)

        viewModel.state.test {
            val initialState = awaitItem()
            assertTrue(initialState.currentNumberOfCharacters == smallNewDescription.length)
        }
    }

    @Test
    fun `should update isButtonEnabled to false when description length is greater than maxNumberOfCharacters`() =
        runTest {
            viewModel.onDescriptionChanged(largeNewDescription)

            viewModel.state.test {
                val initialState = awaitItem()
                assertTrue(!initialState.isButtonEnabled)
            }
        }

    @Test
    fun `should send NavigateBack effect when onBackClick is called`() = runTest {
        viewModel.onBackClick()

        viewModel.effect.test {
            val effect = awaitItem()
            assertTrue(effect is VideoDescriptionEffect.NavigateBack)
        }
    }

    @Test
    fun `should send NavigateToNext effect when onNextClick is called`() = runTest {
        viewModel.onNextClick()

        viewModel.effect.test {
            val effect = awaitItem()
            assertTrue(effect is VideoDescriptionEffect.NavigateToSelectCategories)
        }
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
}