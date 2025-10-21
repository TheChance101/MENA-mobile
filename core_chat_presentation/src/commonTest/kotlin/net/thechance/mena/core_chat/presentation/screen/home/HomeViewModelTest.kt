@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.home

import app.cash.turbine.test
import assertk.assertThat
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
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.domain.repository.MessageRepository
import net.thechance.mena.core_chat.presentation.navigation.ChatEffector
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState
import net.thechance.mena.core_chat.presentation.utils.UiText
import net.thechance.mena.wallet.domain.repository.BalanceRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class HomeViewModelTest {

    private val contactsRepository = mock<ContactsRepository>(MockMode.autofill)
    private val chatRepository = mock<ChatRepository>(MockMode.autofill)
    private val messageRepository = mock<MessageRepository>(MockMode.autofill)
    private val balanceRepository = mock<BalanceRepository>(MockMode.autofill)
    private val effector = mock<ChatEffector>(MockMode.autofill)
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
            assertThat(state.balanceAmount).isEqualTo(expectedBalance)
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
            assertThat(state.balanceAmount).isEqualTo(0.0)
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
    fun `onNewChatClicked should navigate to ContactsRoute when contacts are synced`() = runTest {
        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns createEmptyPagedData()
        everySuspend { contactsRepository.getSyncStatus() } returns true
        everySuspend { effector.navigate(any(), any(), any()) } returns Unit

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onNewChatClicked()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.isSynced).isTrue()
            assertThat(state.isLoading).isFalse()
        }

        verifySuspend { effector.navigate(any(), any(), any()) }
    }

    @Test
    fun `onNewChatClicked should navigate to SyncContactsRoute when contacts are not synced`() = runTest {
        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns createEmptyPagedData()
        everySuspend { contactsRepository.getSyncStatus() } returns false
        everySuspend { effector.navigate(any(), any(), any()) } returns Unit

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onNewChatClicked()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.isSynced).isFalse()
            assertThat(state.isLoading).isFalse()
        }

        verifySuspend { effector.navigate(any(), any(), any()) }
    }

    @Test
    fun `onChatClicked should navigate to ChatDetailsRoute with correct parameters`() = runTest {
        val chat = createTestChat()

        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns createEmptyPagedData()
        everySuspend { effector.navigate(any(), any(), any()) } returns Unit

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onChatClicked(chat)
        advanceUntilIdle()

        verifySuspend { effector.navigate(any(), any(), any()) }
    }

    @Test
    fun `onWalletClicked should navigate to WalletRoute`() = runTest {
        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(any(), any()) } returns createEmptyPagedData()
        everySuspend { effector.navigate(any(), any(), any()) } returns Unit

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onWalletClicked()
        advanceUntilIdle()

        verifySuspend { effector.navigate(any(), any(), any()) }
    }

    @Test
    fun `getChatsSummary should be called with correct page size and number`() = runTest {
        val pageNumber = 0
        val pageSize = 20

        everySuspend { balanceRepository.getBalance() } returns 0.0
        everySuspend { chatRepository.getChatsSummary(pageNumber, pageSize) } returns createEmptyPagedData()

        val viewModel = createViewModel()
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
    fun `chat status should be Received when message is not mine and has no unread count`() = runTest {
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

    private fun createViewModel(): HomeViewModel {
        return HomeViewModel(
            contactsRepository = contactsRepository,
            chatRepository = chatRepository,
            messageRepository = messageRepository,
            balanceRepository = balanceRepository,
            effector = effector,
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
                    time = UiText.DynamicString("12:00"),
                    isMine = true
                ),
                status = ChatUiState.Status.Read
            )
        }
    }
}
