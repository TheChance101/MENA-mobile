package net.thechance.mena.core_chat.data.chat

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.core_chat.data.chat.utils.minusMinutes
import net.thechance.mena.core_chat.data.chat.utils.now
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.domain.exception.NotMenaMemberException
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
class FakeChatRepository : ChatRepository {

    private val chats = mutableMapOf<Uuid, Chat>()
    private val messages = mutableMapOf<Uuid, MutableList<Message>>()
    private val messageFlows = mutableMapOf<Uuid, MutableSharedFlow<Message>>()

    init {
        val chat1Id = Uuid.parse("11111111-1111-1111-1111-111111111111")
        val chat2Id = Uuid.parse("22222222-2222-2222-2222-222222222222")

        val userNoor = Uuid.parse("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
        val userBilal = Uuid.parse("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")
        val otherOne = Uuid.parse("cccccccc-cccc-cccc-cccc-cccccccccccc")

        chats[chat1Id] = Chat(chat1Id, "https://i.ibb.co/fGB4f3cK/tc-logo.png", "General Chat")
        chats[chat2Id] = Chat(chat2Id, "https://picsum.photos/201", "Random Chat")

        messages[chat1Id] = (1..20).map { index ->
            Message(
                id = Uuid.random(),
                senderId = if (index % 2 == 0) userNoor else userBilal,
                chatId = chat1Id,
                text = if (index % 2 == 0) "Noor’s message #$index" else "Bilal’s reply #$index",
                sendAt = LocalDateTime.now().minusMinutes((21 - index)),
                status = when {
                    index < 10 -> MessageStatus.READ
                    index in 10..18 -> MessageStatus.SENT
                    else -> MessageStatus.LOADING
                }
            )
        }.toMutableList()

        messages[chat2Id] = (1..10).map { index ->
            Message(
                id = Uuid.random(),
                senderId = if (index % 2 == 0) userNoor else otherOne,
                chatId = chat2Id,
                text = if (index % 2 == 0) "Random Noor msg #$index" else "Random otherOne msg #$index",
                sendAt = LocalDateTime.now().minusMinutes((21 - index)),
                status = if (index <= 5) MessageStatus.READ else MessageStatus.SENT
            )
        }.toMutableList()
    }


    override suspend fun sendMessage(message: Message) {
        val chatId = message.chatId
        val list = messages.getOrPut(chatId) { mutableListOf() }
        list.add(message)

        val flow = messageFlows.getOrPut(chatId) { MutableSharedFlow(extraBufferCapacity = 64) }
        flow.emit(message)
    }

    override suspend fun loadMessages(chatId: Uuid): List<Message> {
        return messages[chatId]?.toList() ?: emptyList()
    }

    override suspend fun markMessagesAsRead(chatId: Uuid) {
        val updated = messages[chatId]?.map {
            it.copy(status = MessageStatus.READ)
        } ?: return
        messages[chatId] = updated.toMutableList()
    }

    override fun subscribeToMessages(chatId: Uuid): Flow<Message> {
        return messageFlows.getOrPut(chatId) { MutableSharedFlow(extraBufferCapacity = 64) }
    }

    override suspend fun getChatById(chatId: Uuid): Chat {
        return chats.getOrPut(chatId) { Chat(chatId, null, "Mock Chat $chatId") }
    }

    override suspend fun getChatByContactUserId(userId: Uuid): Chat {
        val chatId = messages.entries
            .firstOrNull { (_, msgs) ->
                msgs.any { it.senderId == userId }
            }?.key
        val contactMenaMemberId = Uuid.parse("cccccccc-dddd-cccc-cccc-cccccccccccc")

        if (userId == contactMenaMemberId) return Chat(
            Uuid.random(),
            "https://picsum.photos/201",
            "First Time Chat"
        )

        return chatId?.let { chats[it] }
            ?: throw NotMenaMemberException(logMessage = "Can not get Chat for non MENA member")
    }
}