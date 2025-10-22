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

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onDescriptionChanged should update description when called`() = runTest {
        viewModel.onDescriptionChanged(SMALL_NEW_DESCRIPTION)

        viewModel.state.test {
            val initialState = awaitItem()
            assertTrue(initialState.description == SMALL_NEW_DESCRIPTION)
        }
    }

    @Test
    fun `onDescriptionChanged should update currentNumberOfCharacters when called`() = runTest {
        viewModel.onDescriptionChanged(SMALL_NEW_DESCRIPTION)

        viewModel.state.test {
            val initialState = awaitItem()
            assertTrue(initialState.currentNumberOfCharacters == SMALL_NEW_DESCRIPTION.length)
        }
    }

    @Test
    fun `should update isButtonEnabled to false when description length is greater than maxNumberOfCharacters`() =
        runTest {
            val largeNewDescription = "istanbul ".repeat(500)

            viewModel.onDescriptionChanged(largeNewDescription)

            viewModel.state.test {
                val initialState = awaitItem()
                assertTrue(!initialState.isButtonEnabled)
            }
        }

    @Test
    fun `onClickBack should send NavigateBack effect when called`() = runTest {
        viewModel.onClickBack()

        viewModel.effect.test {
            val effect = awaitItem()
            assertTrue(effect is VideoDescriptionEffect.NavigateBack)
        }
    }

    @Test
    fun `onClickNext should send NavigateToNext effect when called`() = runTest {
        viewModel.onClickNext()

        viewModel.effect.test {
            val effect = awaitItem()
            assertTrue(effect is VideoDescriptionEffect.NavigateToSelectCategories)
        }
    }

    private companion object {
        const val SMALL_NEW_DESCRIPTION = "New Description"
    }
}