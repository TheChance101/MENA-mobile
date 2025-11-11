@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.home

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.model.SyncState
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.domain.repository.MessageRepository
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState
import net.thechance.mena.core_chat.presentation.utils.now
import net.thechance.mena.wallet.domain.repository.BalanceRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class HomeViewModelTest {

    private val contactsRepository = mock<ContactsRepository>(MockMode.autofill)
    private val chatRepository = mock<ChatRepository>(MockMode.autofill)
    private val balanceRepository = mock<BalanceRepository>(MockMode.autofill)

    private val messageRepository = mock<MessageRepository>(MockMode.autofill)
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
    fun `observeChatSummariesList should update state with new chat summaries`() = runTest {
        val chatFlow = MutableSharedFlow<List<ChatSummary>>(replay = 1)
        everySuspend { chatRepository.observeChatSummaries() } returns chatFlow
        everySuspend { chatRepository.observeChatSummariesSyncState() } returns flowOf()
        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns createEmptyPagedData()

        val chat1 = createChatSummary(name = "Chat 1")
        val chat2 = createChatSummary(name = "Chat 2")

        val viewModel = createViewModel()
        advanceUntilIdle()

        chatFlow.emit(listOf(chat1, chat2))
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.chats.size).isEqualTo(2)
        }
    }

    @Test
    fun `observeChatSummariesList should sort chats by last message time descending`() = runTest {
        val chatFlow = MutableSharedFlow<List<ChatSummary>>(replay = 1)
        everySuspend { chatRepository.observeChatSummaries() } returns chatFlow
        everySuspend { chatRepository.observeChatSummariesSyncState() } returns flowOf()
        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns createEmptyPagedData()

        val olderChat = createChatSummary(
            name = "Older",
            sendAt = LocalDateTime(2024, 1, 1, 10, 0)
        )
        val newerChat = createChatSummary(
            name = "Newer",
            sendAt = LocalDateTime(2024, 1, 2, 10, 0)
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        chatFlow.emit(listOf(olderChat, newerChat))
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.chats.first().name).isEqualTo("Newer")
            assertThat(state.chats.last().name).isEqualTo("Older")
        }
    }

    @Test
    fun `observeChatSummariesList should remove duplicates by id`() = runTest {
        val chatFlow = MutableSharedFlow<List<ChatSummary>>(replay = 1)
        everySuspend { chatRepository.observeChatSummaries() } returns chatFlow
        everySuspend { chatRepository.observeChatSummariesSyncState() } returns flowOf()
        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns createEmptyPagedData()

        val chatId = Uuid.random()
        val chat1 = createChatSummary(id = chatId, name = "Chat 1")
        val chat2 = createChatSummary(id = chatId, name = "Chat 1 Updated")

        val viewModel = createViewModel()
        advanceUntilIdle()

        chatFlow.emit(listOf(chat1, chat2))
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.chats.size).isEqualTo(1)
        }
    }

    @Test
    fun `observeChatSummariesList should clear chats when empty list is emitted`() = runTest {
        val chatFlow = MutableSharedFlow<List<ChatSummary>>(replay = 1)
        everySuspend { chatRepository.observeChatSummaries() } returns chatFlow
        everySuspend { chatRepository.observeChatSummariesSyncState() } returns flowOf()
        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns createEmptyPagedData()

        val chat = createChatSummary()

        val viewModel = createViewModel()
        advanceUntilIdle()

        chatFlow.emit(listOf(chat))
        advanceUntilIdle()

        chatFlow.emit(emptyList())
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.chats).isEmpty()
        }
    }

    @Test
    fun `mergedChats should preserve existing chats and add new ones`() = runTest {
        val chatFlow = MutableSharedFlow<List<ChatSummary>>(replay = 1)
        everySuspend { chatRepository.observeChatSummaries() } returns chatFlow
        everySuspend { chatRepository.observeChatSummariesSyncState() } returns flowOf()
        everySuspend { balanceRepository.getBalance() } returns 0.0

        val existingChat = createChatSummary(name = "Existing")
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns PagedData(
            data = listOf(existingChat),
            totalItems = 1,
            isLastPage = true
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        val newChat = createChatSummary(name = "New")

        chatFlow.emit(listOf(existingChat, newChat))
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.chats.size).isEqualTo(2)
        }
    }

    @Test
    fun `mergedChats should update existing chat with newer data`() = runTest {
        val chatFlow = MutableSharedFlow<List<ChatSummary>>(replay = 1)
        everySuspend { chatRepository.observeChatSummaries() } returns chatFlow
        everySuspend { chatRepository.observeChatSummariesSyncState() } returns flowOf()
        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns createEmptyPagedData()

        val chatId = Uuid.random()
        val oldChat = createChatSummary(id = chatId, content = "Old message")
        val updatedChat = createChatSummary(id = chatId, content = "New message")

        val viewModel = createViewModel()
        advanceUntilIdle()

        chatFlow.emit(listOf(oldChat))
        advanceUntilIdle()

        chatFlow.emit(listOf(updatedChat))
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.chats.size).isEqualTo(1)
            assertThat(state.chats.first().lastMessage?.text).isEqualTo("New message")
        }
    }



    @Test
    fun `observeChatSummariesSyncState should not show snackbar on ChatsSummariesSyncedSuccess`() = runTest {
        val syncFlow = MutableSharedFlow<SyncState>()
        everySuspend { chatRepository.observeChatSummariesSyncState() } returns syncFlow
        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns createEmptyPagedData()

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            syncFlow.emit(SyncState.ChatsSummariesSyncedSuccess)
            advanceUntilIdle()

            expectNoEvents()
        }
    }

    @Test
    fun `observeChatSummariesSyncState should not show snackbar on DeletedChatsSyncedSuccess`() = runTest {
        val syncFlow = MutableSharedFlow<SyncState>()
        everySuspend { chatRepository.observeChatSummariesSyncState() } returns syncFlow
        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns createEmptyPagedData()

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            syncFlow.emit(SyncState.DeletedChatsSyncedSuccess)
            advanceUntilIdle()

            expectNoEvents()
        }
    }

    @Test
    fun `init should load balance amount when viewModel is created`() = runTest {
        val expectedBalance = 100.0
        everySuspend { balanceRepository.getBalance() } returns expectedBalance
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns createEmptyPagedData()

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.balanceAmount).isEqualTo(expectedBalance.toInt().toString())
        }
    }



    @Test
    fun `init should set isLoading to false after successfully loading chats`() = runTest {
        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns createEmptyPagedData()

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `init should handle balance loading error gracefully`() = runTest {
        val exception = RuntimeException("Balance fetch failed")
        everySuspend { balanceRepository.getBalance() } throws exception
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns createEmptyPagedData()

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.balanceAmount).isEqualTo("--")
        }
    }



    @Test
    fun `onLoadChatsSummaryError should set isLoading to false`() = runTest {
        val exception = RuntimeException("Network error")
        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } throws exception

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `onNewChatClicked should emit NavigateToContacts effect when contacts are synced`() = runTest {
        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns createEmptyPagedData()
        everySuspend { contactsRepository.getHasUserSyncedContactsStatus() } returns true

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onNewChatClicked()
            advanceUntilIdle()

            assertEquals(HomeScreenEffect.NavigateToContacts, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onNewChatClicked should emit NavigateToSyncContacts effect when contacts are not synced`() =
        runTest {
            everySuspend { balanceRepository.getBalance() } returns 0.0
            everySuspend {
                chatRepository.getChatsSummary(any(), any())
            } returns createEmptyPagedData()
            everySuspend { contactsRepository.getHasUserSyncedContactsStatus() } returns false

            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.effect.test {
                viewModel.onNewChatClicked()
                advanceUntilIdle()

                assertEquals(HomeScreenEffect.NavigateToSyncContacts, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onChatClicked should emit NavigateToChat effect with correct parameters`() = runTest {
        val chat = createTestChat()
        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns createEmptyPagedData()
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onChatClicked(chat)
            advanceUntilIdle()

            assertEquals(
                HomeScreenEffect.NavigateToChat(
                    chatId = chat.id.toString(),
                    chatName = chat.name
                ), awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onWalletClicked should emit NavigateToWallet effect`() = runTest {
        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns createEmptyPagedData()

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onWalletClicked()
            advanceUntilIdle()

            assertEquals(HomeScreenEffect.NavigateToWallet, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getChatsSummary should be called with correct page size and number`() = runTest {
        val pageNumber = 0
        val pageSize = 20

        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend {
            chatRepository.getChatsSummary(
                pageNumber,
                pageSize
            )
        } returns createEmptyPagedData()

        createViewModel()
        advanceUntilIdle()

        verifySuspend(exactly(1)) { chatRepository.getChatsSummary(pageNumber, pageSize) }
    }






    @Test
    fun `observeChatSummariesSyncState should show no internet snackbar when Offline is emitted`() = runTest {
        val syncFlow = MutableSharedFlow<SyncState>()
        everySuspend { chatRepository.observeChatSummariesSyncState() } returns syncFlow
        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns createEmptyPagedData()

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            syncFlow.emit(SyncState.Offline)
            advanceUntilIdle()

            val effect = awaitItem()
            assertThat(effect is HomeScreenEffect.ShowSnackBar)
        }
    }

    @Test
    fun `observeChatSummariesSyncState should show error snackbar when Error is emitted`() = runTest {
        val syncFlow = MutableSharedFlow<SyncState>()
        everySuspend { chatRepository.observeChatSummariesSyncState() } returns syncFlow
        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns createEmptyPagedData()

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            syncFlow.emit(SyncState.Error(error = RuntimeException("Test error")))
            advanceUntilIdle()

            val effect = awaitItem()
            assertThat(effect is HomeScreenEffect.ShowSnackBar)
        }
    }

    @Test
    fun `error loading chats should emit snackbar effect`() = runTest {
        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } throws RuntimeException("Network error")

        val viewModel = createViewModel()

        viewModel.effect.test {
            advanceUntilIdle()

            val effect = awaitItem()
            assertThat(effect is HomeScreenEffect.ShowSnackBar).isTrue()
            if (effect is HomeScreenEffect.ShowSnackBar) {
                assertThat(effect.snackBarData.isError).isTrue()
            }
        }
    }



    private fun createViewModel(): HomeViewModel {
        return HomeViewModel(
            contactsRepository = contactsRepository,
            chatRepository = chatRepository,
            balanceRepository = balanceRepository,
            messageRepository = messageRepository,

            dispatcher = testDispatcher
        )
    }

    private fun createChatSummary(
        id: Uuid = Uuid.random(),
        name: String = "Test Chat",
        imageUrl: String = "https://example.com/image.jpg",
        content: String = "Test message",
        sendAt: LocalDateTime = LocalDateTime(2024, 1, 1, 12, 0),
        isMine: Boolean = true,
        unReadCount: Int = 0
    ): ChatSummary {
        return ChatSummary(
            id = id,
            name = name,
            imageUrl = imageUrl,
            lastMessage = ChatSummary.Message(
                content = content,
                sendAt = sendAt,
                isMine = isMine
            ),
            unReadMessagesCount = unReadCount
        )
    }

    private fun createEmptyPagedData(): PagedData<ChatSummary> {
        return PagedData(
            data = emptyList(),
            totalItems = 0,
            isLastPage = true
        )
    }

    companion object {
        private fun createTestChat(): ChatUiState {
            val chatId = Uuid.random()
            val chatName = "Test Chat"
            return ChatUiState(
                id = chatId,
                name = chatName,
                imageUrl = null,
                lastMessage = ChatUiState.MessageUiState(
                    text = "Hello",
                    time = LocalDateTime.now(),
                    isMine = true
                ),
                status = ChatUiState.Status.Read
            )
        }
    }
}
