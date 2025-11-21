@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class, DelicateMokkeryApi::class)

package net.thechance.mena.core_chat.presentation.screen.contacts

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isNotNull
import dev.mokkery.annotations.DelicateMokkeryApi
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.error
import mena.core_chat_presentation.generated.resources.something_went_wrong
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.Contact
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.presentation.components.snackBarHost.SnackBarData
import net.thechance.mena.core_chat.presentation.screen.syncContacts.IS_SYNC_SUCCESS
import net.thechance.mena.core_chat.presentation.utils.UiText
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ContactsViewModelTest {
    private val contactsRepository = mock<ContactsRepository>()
    private val chatRepository = mock<ChatRepository>()

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should load contacts successfully when viewModel is created`() = runTest {
        everySuspend { contactsRepository.getUserContacts(any()) } returns pagedContacts

        val viewModel = createViewModel()

        viewModel.state.test {
            val state = awaitItem()
            val contacts = state.contacts.first()
            assertThat(contacts).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `onRefreshContactsClicked should load contacts when called`() =
        runTest {
            everySuspend { contactsRepository.getUserContacts(any()) } returns pagedContacts
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onRefreshContactsClicked()
            advanceUntilIdle()

            viewModel.state.test {
                val state = awaitItem()
                assertThat(state.contacts).isNotNull()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onBackClicked should emit NavigateBack effect when invoked`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onBackClicked()
            advanceUntilIdle()

            assertEquals(ContactsScreenEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onReSyncClicked should emit NavigateToSyncContacts effect when invoked`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onReSyncClicked()
            advanceUntilIdle()

            assertEquals(ContactsScreenEffect.NavigateToSyncContacts, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onContactClicked should get chat with correct contact when called`() = runTest {
        val contactId = Uuid.random()
        everySuspend { chatRepository.getChatByOtherUserId(any()) } returns chat
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onContactClicked(contactId)
        advanceUntilIdle()

        verifySuspend { chatRepository.getChatByOtherUserId(contactId) }
    }

    @Test
    fun `onContactClicked should emit NavigateToChat effect with correct parameters`() =
        runTest {
            val contactId = Uuid.random()
            everySuspend { contactsRepository.getUserContacts(any()) } returns pagedContacts
            everySuspend { chatRepository.getChatByOtherUserId(any()) } returns chat

            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.effect.test {
                viewModel.onContactClicked(contactId)
                advanceUntilIdle()

                assertEquals(
                    ContactsScreenEffect.NavigateToChat(
                        chatId = chat.id.toString(),
                        chatName = chat.name
                    ), awaitItem()
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onContactClicked should emit snackBar effect when repository throws error`() = runTest {
        everySuspend { chatRepository.getChatByOtherUserId(any()) } throws Exception()
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onContactClicked(Uuid.random())
            advanceUntilIdle()

            assertEquals(
                ContactsScreenEffect.ShowSnackBar(
                    SnackBarData(
                        title = UiText.StringRes(Res.string.error),
                        message = UiText.StringRes(Res.string.something_went_wrong),
                        isError = true
                    )
                ), awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeSyncSuccess should refresh contacts`() =
        runTest {
            everySuspend { contactsRepository.getUserContacts(any()) } returns pagedContacts
            val fakeFlow = MutableSharedFlow<Map<String, Any>>()

            val viewModel = createViewModel()

            advanceUntilIdle()

            fakeFlow.emit(mapOf(IS_SYNC_SUCCESS to true))
            advanceUntilIdle()

            viewModel.state.test {
                val state = awaitItem()
                assertThat(state.contacts).isNotNull()
                cancelAndIgnoreRemainingEvents()
            }
        }

    private fun createViewModel(): ContactsViewModel {
        return ContactsViewModel(contactsRepository, chatRepository, testDispatcher)
    }

    private companion object {
        val contact = Contact(
            firstName = "John",
            lastName = "Doe",
            phone = "123456789",
            imageUrl = null,
            menaUserId = Uuid.random()
        )

        val pagedContacts = PagedData(
            data = listOf(contact),
            totalItems = 1,
            isLastPage = true
        )

        val chat = Chat(
            id = Uuid.random(),
            imageUrl = null,
            name = "John Doe",
            requesterId = Uuid.random(),
            receiverId = Uuid.random()
        )
    }
}