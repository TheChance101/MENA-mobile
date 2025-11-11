package net.thechance.mena.core_chat.data.source.local.database.cachedChatSummary

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import net.thechance.mena.core_chat.data.source.remote.dto.ChatSummaryDto
import kotlin.uuid.ExperimentalUuidApi

@Dao
interface CachedChatSummaryDao {
    @Upsert
    suspend fun insertChatSummary(chatSummary: CachedChatSummaryDto)

    @Query("SELECT * FROM cached_chat_summary WHERE last_message IS NOT NULL ORDER BY last_message_send_at DESC LIMIT :pageSize OFFSET :offset")
    suspend fun getChatSummaries(pageSize: Int, offset: Int): List<CachedChatSummaryDto>

    @Query("SELECT * FROM cached_chat_summary ORDER BY last_message_send_at DESC")
    fun getChatSummariesFlow(): Flow<List<CachedChatSummaryDto>>
    @Query("SELECT COUNT(*) FROM cached_chat_summary")
    suspend fun getChatSummariesCount(): Int

    @Upsert
    suspend fun insertMultipleChatSummaries(chatSummaries: List<CachedChatSummaryDto>)

    @OptIn(ExperimentalUuidApi::class)
    @Query("DELETE FROM cached_chat_summary WHERE id IN (:chatsId)")
    suspend fun deleteMultipleChatSummaries(chatsId: List<String>)

    @Query("DELETE FROM cached_chat_summary WHERE id = :chatId")
    suspend fun deleteChatSummaryById(chatId: String)

    @Query("SELECT * FROM cached_chat_summary WHERE id = :chatId")
    suspend fun getChatSummaryById(chatId: String): CachedChatSummaryDto?

    @Query("UPDATE cached_chat_summary SET unread_message_count = :readCount WHERE id = :chatId")
    suspend fun updateUnReadMessagesCountByChatId(chatId: String, readCount: Int)
}