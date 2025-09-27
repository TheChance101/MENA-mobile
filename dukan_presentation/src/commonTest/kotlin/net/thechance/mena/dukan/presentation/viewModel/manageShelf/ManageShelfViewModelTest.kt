package net.thechance.mena.dukan.presentation.viewModel.manageShelf

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.dukan.presentation.screen.manageShelf.ManageShelfArgs
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class ManageShelfViewModelTest {
    private lateinit var manageShelfViewModel: ManageShelfViewModel
    private lateinit var savedStateHandle: SavedStateHandle
    private val testDispatcher = StandardTestDispatcher()
    private val expectedShelfId = "1"
    private val expectedShelfTitle = "Clothing"

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle()
        savedStateHandle[ManageShelfArgs.shelfId] = expectedShelfId
        savedStateHandle[ManageShelfArgs.shelfTitle] = expectedShelfTitle
        manageShelfViewModel = ManageShelfViewModel(savedStateHandle, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should initialize state with shelf id and title from saved state handle`() {
        val state = manageShelfViewModel.state.value
        assertEquals(expectedShelfTitle, state.shelfTitle)
    }

    @Test
    fun `init should throw exception when shelf id is null`() {
        val failingSavedStateHandle = savedStateHandle
        failingSavedStateHandle[ManageShelfArgs.shelfId] = null

        assertFailsWith<IllegalArgumentException> {
            ManageShelfViewModel(failingSavedStateHandle, testDispatcher)
        }
    }

    @Test
    fun `init should throw exception when shelf title is null`() {
        val failingSavedStateHandle = savedStateHandle
        failingSavedStateHandle[ManageShelfArgs.shelfTitle] = null

        assertFailsWith<IllegalArgumentException> {
            ManageShelfViewModel(failingSavedStateHandle, testDispatcher)
        }
    }

    @Test
    fun `onBackClicked should emit NavigateBack effect`() = runTest {
        manageShelfViewModel.onBackClicked()

        val actualEffect = manageShelfViewModel.effect.first()

        assertEquals(ManageShelfEffect.NavigateBack, actualEffect)
    }


    @Test
    fun `onDeleteClicked should emit DeleteShelf effect with current shelfId`() = runTest {
        manageShelfViewModel.onDeleteClicked()

        val actualEffect = manageShelfViewModel.effect.first()

        val expectedEffect = ManageShelfEffect.DeleteShelf(shelfId = expectedShelfId)
        assertEquals(expectedEffect, actualEffect)
    }

}