package net.thechance.mena.dukan.presentation.viewModel.createDukan

import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.thechance.mena.dukan.domain.entity.Category
import net.thechance.mena.dukan.domain.entity.Color
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.repository.DukanRepository
import net.thechance.mena.dukan.domain.repository.LocationRepository
import org.junit.Test
import kotlin.test.assertEquals

class CreateDukanViewModelTest {

    private val dukanRepository = mock<DukanRepository>(mode = MockMode.autofill)
    private val locationRepository = mock<LocationRepository>(mode = MockMode.autofill)
    private lateinit var viewModel: CreateDukanViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init should load styles and colors`() = runTest {

        everySuspend { dukanRepository.getDukanStyles() } returns fakeDukanStyle()
        everySuspend { dukanRepository.getDukanColors() } returns fakeDukanColor()
        everySuspend { dukanRepository.getCategories() } returns fakeCategories()

        viewModel = CreateDukanViewModel(dukanRepository, locationRepository)

        advanceUntilIdle()
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(fakeCategories().size, state.dukanCategories.size)
            assertEquals(fakeDukanStyle().size, state.dukanStyles.size)
            assertEquals(fakeDukanColor().size, state.dukanColors.size)

        }

    }

    @Test
    fun ` when onColorClicked is called, then the ViewModel's state of selectedColor should be updated`() =
        runTest {

            val color = ColorUiState(
                id = "1",
                color = 0xFFF545
            )
            viewModel = CreateDukanViewModel(dukanRepository, locationRepository)

            viewModel.onColorClicked(color)

            viewModel.state.test {
                val state = awaitItem()
                assertEquals(color, state.selectedColor)
                cancelAndIgnoreRemainingEvents()
            }

        }

    @Test
    fun `when onStyleClicked is called, then the ViewModel's state of selectedStyle should be updated`() =
        runTest {

            val style = Dukan.Style.WIDE_IMAGE

            viewModel = CreateDukanViewModel(dukanRepository, locationRepository)

            viewModel.onStyleClicked(style)

            viewModel.state.test {
                val state = awaitItem()
                assertEquals(style, state.selectedStyle)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Given selectedStyle is not null and selectedColor is not null, when updateCreateButtonState is called, then isButtonEnabled should be true`() =
        runTest {
            val style = Dukan.Style.WIDE_IMAGE
            val color = ColorUiState(
                id = "1",
                color = 0xFFF545
            )

            viewModel = CreateDukanViewModel(dukanRepository, locationRepository)

            viewModel.onStyleClicked(style)
            viewModel.onColorClicked(color)

            viewModel.updateCreateButtonState()

            viewModel.state.test {
                val state = awaitItem()
                assertEquals(true, state.isButtonEnabled)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Given selectedStyle is null and selectedColor is not null, when updateCreateButtonState is called, then isButtonEnabled should be false`() =
        runTest {

            val color = ColorUiState(
                id = "1",
                color = 0xFFF545
            )

            viewModel = CreateDukanViewModel(dukanRepository, locationRepository)

            viewModel.onColorClicked(color)

            viewModel.updateCreateButtonState()

            viewModel.state.test {
                val state = awaitItem()
                assertEquals(false, state.isButtonEnabled)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Given selectedStyle is not null and selectedColor is null, when updateCreateButtonState is called, then isButtonEnabled should be false`() =
        runTest {
            val style = Dukan.Style.WIDE_IMAGE

            viewModel = CreateDukanViewModel(dukanRepository, locationRepository)

            viewModel.onStyleClicked(style)

            viewModel.updateCreateButtonState()

            viewModel.state.test {
                val state = awaitItem()
                assertEquals(false, state.isButtonEnabled)
                cancelAndIgnoreRemainingEvents()
            }

        }

    @Test
    fun `Given selectedStyle is null and selectedColor is null, when updateCreateButtonState is called, then isButtonEnabled should be false`() =
        runTest {
            viewModel = CreateDukanViewModel(dukanRepository, locationRepository)

            viewModel.updateCreateButtonState()

            viewModel.state.test {
                val state = awaitItem()
                assertEquals(false, state.isButtonEnabled)
                cancelAndIgnoreRemainingEvents()
            }
        }
}


private fun fakeDukanColor(): List<Color> {
    return listOf(
        Color(
            id = "1",
            hexCode = "#F77053"
        ),
        Color(
            id = "2",
            hexCode = "#F4C343"
        ),
        Color(
            id = "3",
            hexCode = "#C30C30"
        ),
        Color(
            id = "4",
            hexCode = "#30ABE8"
        ),
    )
}


private fun fakeDukanStyle(): List<Dukan.Style> {
    return listOf(
        Dukan.Style.WIDE_IMAGE,
        Dukan.Style.SMALL_IMAGE,
        Dukan.Style.NO_IMAGE
    )
}

private fun fakeCategories(): List<Category> {
    return listOf(
        Category(
            id = "1",
            name = "Electronics",
            imageUrl = "https://example.com/electronics.png"
        ),
        Category(
            id = "2",
            name = "Clothes",
            imageUrl = "https://example.com/clothes.png"
        ),
        Category(
            id = "3",
            name = "Groceries",
            imageUrl = "https://example.com/groceries.png"
        ),
        Category(
            id = "4",
            name = "Books",
            imageUrl = "https://example.com/books.png"
        )
    )
}
