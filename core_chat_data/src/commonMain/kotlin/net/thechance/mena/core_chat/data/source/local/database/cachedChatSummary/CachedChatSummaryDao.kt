package net.thechance.mena.core_chat.data.source.local.database.cachedChatSummary

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface CachedChatSummaryDao {
    @Upsert
    suspend fun insertChatSummary(chatSummary: CachedChatSummaryDto)

    @Upsert
    suspend fun insertMultipleChatSummaries(chatSummaries: List<CachedChatSummaryDto>)

    @Delete
    suspend fun deleteChatSummary(chatSummary: CachedChatSummaryDto)

    @Query("DELETE FROM cached_chat_summary WHERE id = :chatId")
    suspend fun deleteChatSummaryById(chatId: String)

    @Query("SELECT * FROM cached_chat_summary WHERE id = :chatId")
    fun getChatSummaryById(chatId: String): CachedChatSummaryDto?
}