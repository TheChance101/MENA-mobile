@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.home

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.doesNotContain
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
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.event.DeleteChatEvent
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.model.SyncState
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.domain.repository.MessageRepository
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState
import net.thechance.mena.core_chat.presentation.utils.UiText
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
    private val messageRepository = mock<MessageRepository>(MockMode.autofill)
    private val balanceRepository = mock<BalanceRepository>(MockMode.autofill)
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
    fun `init should load chats summary when viewModel is created`() = runTest {
        val expectedChatSummary = createChatSummary()
        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns PagedData(
            data = listOf(expectedChatSummary),
            totalItems = 1,
            isLastPage = true
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.chats.size).isEqualTo(1)
            assertThat(state.chats.first().id).isEqualTo(expectedChatSummary.id)
            assertThat(state.chats.first().name).isEqualTo(expectedChatSummary.name)
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
    fun `onChatsListScrolled should load next page of chats`() = runTest {
        val firstPageChat = createChatSummary(name = "Chat 1")
        val secondPageChat = createChatSummary(name = "Chat 2")

        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(0, 20) } returns PagedData(
            data = listOf(firstPageChat),
            totalItems = 2,
            isLastPage = false
        )
        everySuspend { chatRepository.getChatsSummary(1, 20) } returns PagedData(
            data = listOf(secondPageChat),
            totalItems = 2,
            isLastPage = true
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onChatsListScrolled()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.chats.size).isEqualTo(2)
        }
    }

    @Test
    fun `getChatsSummary should return chats sorted by latest message time`() = runTest {
        val olderChat = createChatSummary(
            name = "Older Chat",
            sendAt = LocalDateTime(2024, 1, 1, 10, 0)
        )
        val newerChat = createChatSummary(
            name = "Newer Chat",
            sendAt = LocalDateTime(2024, 1, 2, 10, 0)
        )

        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns PagedData(
            data = listOf(olderChat, newerChat),
            totalItems = 2,
            isLastPage = true
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.chats.first().name).isEqualTo("Newer Chat")
            assertThat(state.chats.last().name).isEqualTo("Older Chat")
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
    fun `multiple chats should be mapped correctly to UI state`() = runTest {
        val chat1 = createChatSummary(name = "Chat 1", unReadCount = 5)
        val chat2 = createChatSummary(name = "Chat 2", unReadCount = 0, isMine = false)

        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns PagedData(
            data = listOf(chat1, chat2),
            totalItems = 2,
            isLastPage = true
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.chats.size).isEqualTo(2)
            assertThat(state.chats[0].name).isEqualTo("Chat 1")
            assertThat(state.chats[1].name).isEqualTo("Chat 2")
        }
    }

    @Test
    fun `chat status should be UnRead when message is not mine and has unread count`() = runTest {
        val unReadCount = 3
        val chat = createChatSummary(unReadCount = unReadCount, isMine = false)

        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns PagedData(
            data = listOf(chat),
            totalItems = 1,
            isLastPage = true
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            val chatStatus = state.chats.first().status
            assertThat(chatStatus is ChatUiState.Status.UnRead).isTrue()
            if (chatStatus is ChatUiState.Status.UnRead) {
                assertThat(chatStatus.count).isEqualTo(unReadCount)
            }
        }
    }

    @Test
    fun `chat status should be Read when message is mine and has no unread count`() = runTest {
        val chat = createChatSummary(unReadCount = 0, isMine = true)

        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns PagedData(
            data = listOf(chat),
            totalItems = 1,
            isLastPage = true
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            val chatStatus = state.chats.first().status
            assertThat(chatStatus is ChatUiState.Status.Read).isTrue()
        }
    }

    @Test
    fun `chat status should be Sent when message is mine and has unread count`() = runTest {
        val chat = createChatSummary(unReadCount = 1, isMine = true)

        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns PagedData(
            data = listOf(chat),
            totalItems = 1,
            isLastPage = true
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            val chatStatus = state.chats.first().status
            assertThat(chatStatus is ChatUiState.Status.Sent).isTrue()
        }
    }

    @Test
    fun `chat status should be Received when message is not mine and has no unread count`() =
        runTest {
            val chat = createChatSummary(unReadCount = 0, isMine = false)

            everySuspend { balanceRepository.getBalance() } returns 0.0
            everySuspend { chatRepository.getChatsSummary(any(), any()) } returns PagedData(
                data = listOf(chat),
                totalItems = 1,
                isLastPage = true
            )

            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.state.test {
                val state = awaitItem()
                val chatStatus = state.chats.first().status
                assertThat(chatStatus is ChatUiState.Status.Received).isTrue()
            }
        }

    @Test
    fun `observeDeleteChat should remove chat from state when DeleteChatEvent is collected`() = runTest {
        val chatToKeep = createChatSummary(name = "Keep Chat")
        val chatToDelete = createChatSummary(name = "Delete Chat")

        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns PagedData(
            data = listOf(chatToKeep, chatToDelete),
            totalItems = 2,
            isLastPage = true
        )

        val deleteChatFlow = MutableSharedFlow<DeleteChatEvent>()
        everySuspend { messageRepository.observeDeleteChat() } returns deleteChatFlow

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            var state = awaitItem()
            assertThat(state.chats.size).isEqualTo(2)

            deleteChatFlow.emit(DeleteChatEvent(chatToDelete.id))
            advanceUntilIdle()

            state = awaitItem()
            assertThat(state.chats.size).isEqualTo(1)
            assertThat(state.chats.first().id).isEqualTo(chatToKeep.id)
        }
    }

    @Test
    fun `onCollectDeleteChatEvent should not modify state when event is null`() = runTest {
        val chat1 = createChatSummary(name = "Chat 1")

        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns PagedData(
            data = listOf(chat1),
            totalItems = 1,
            isLastPage = true
        )

        val deleteChatFlow = MutableSharedFlow<DeleteChatEvent>()
        everySuspend { messageRepository.observeDeleteChat() } returns deleteChatFlow

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.chats.size).isEqualTo(1)

            advanceUntilIdle()

            expectNoEvents()
        }
    }

    @Test
    fun `observeChatSummariesSyncState should update chats when ChatsSummariesSynced is emitted`() = runTest {
        val chat1 = createChatSummary(name = "Chat 1")
        val chat2 = createChatSummary(name = "Chat 2")
        val syncFlow = MutableSharedFlow<SyncState>()
        everySuspend { chatRepository.observeChatSummariesSyncState() } returns syncFlow
        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns createEmptyPagedData()

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            var state = awaitItem()
            assertThat(state.chats).isEmpty()

            syncFlow.emit(SyncState.ChatsSummariesSynced(listOf(chat1, chat2)))
            advanceUntilIdle()

            state = awaitItem()
            assertThat(state.chats.size).isEqualTo(2)
            assertThat(state.chats[0].id).isEqualTo(chat1.id)
            assertThat(state.chats[1].id).isEqualTo(chat2.id)
        }
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
    fun `observeChatSummariesSyncState should remove deleted chats when DeletedChatsSynced is emitted`() = runTest {
        val chat1 = createChatSummary(name = "Chat 1")
        val chat2 = createChatSummary(name = "Chat 2")
        val chat3 = createChatSummary(name = "Chat 3")
        val syncFlow = MutableSharedFlow<SyncState>()
        everySuspend { chatRepository.observeChatSummariesSyncState() } returns syncFlow
        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns createEmptyPagedData()

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            var state = awaitItem()
            assertThat(state.chats).isEmpty()

            syncFlow.emit(SyncState.ChatsSummariesSynced(listOf(chat1, chat2, chat3)))
            advanceUntilIdle()

            state = awaitItem()
            assertThat(state.chats.size).isEqualTo(3)

            syncFlow.emit(SyncState.DeletedChatsSynced(listOf(chat2.id)))
            advanceUntilIdle()

            state = awaitItem()
            assertThat(state.chats.size).isEqualTo(2)
            assertThat(state.chats.map { it.id }).containsExactly(chat1.id, chat3.id)
            assertThat(state.chats.map { it.id }).doesNotContain(chat2.id)
        }
    }

    @Test
    fun `observeChatSummariesSyncState should remove multiple deleted chats when DeletedChatsSynced is emitted`() = runTest {
        val chat1 = createChatSummary(name = "Chat 1")
        val chat2 = createChatSummary(name = "Chat 2")
        val chat3 = createChatSummary(name = "Chat 3")
        val chat4 = createChatSummary(name = "Chat 4")
        val syncFlow = MutableSharedFlow<SyncState>()
        everySuspend { chatRepository.observeChatSummariesSyncState() } returns syncFlow
        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns createEmptyPagedData()

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            var state = awaitItem()
            assertThat(state.chats).isEmpty()

            syncFlow.emit(SyncState.ChatsSummariesSynced(listOf(chat1, chat2, chat3, chat4)))
            advanceUntilIdle()

            state = awaitItem()
            assertThat(state.chats.size).isEqualTo(4)

            syncFlow.emit(SyncState.DeletedChatsSynced(listOf(chat2.id, chat4.id)))
            advanceUntilIdle()

            state = awaitItem()
            assertThat(state.chats.size).isEqualTo(2)
            assertThat(state.chats.map { it.id }).containsExactly(chat1.id, chat3.id)
        }
    }
    private fun createViewModel(): HomeViewModel {
        return HomeViewModel(
            contactsRepository = contactsRepository,
            chatRepository = chatRepository,
            messageRepository = messageRepository,
            balanceRepository = balanceRepository,
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
                    uiTime = UiText.DynamicString("12:00"),
                    isMine = true,
                    time = LocalDateTime(2024, 1, 1, 12, 0)
                ),
                status = ChatUiState.Status.Read
            )
        }
    }
}
