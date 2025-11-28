package net.thechance.mena.core_chat.data.source.local.database.cachedMessage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import net.thechance.mena.core_chat.domain.entity.MessageStatus

@Dao
interface CachedMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMessages(message: List<CachedMessageLocalDto>)

    @Query("SELECT * FROM cached_messages WHERE chat_id = :chatId ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getMessagesByChatIdWithOffset(
        chatId: String,
        limit: Int,
        offset: Int
    ): List<CachedMessageLocalDto>

    @Query("SELECT COUNT(*) FROM cached_messages WHERE chat_id = :chatId")
    suspend fun getTotalMessagesCount(chatId: String): Int

    @Query("SELECT * FROM cached_messages WHERE id = :messageId LIMIT 1")
    suspend fun getMessageById(messageId: String): CachedMessageLocalDto?

    @Update
    suspend fun updateMessage(message: CachedMessageLocalDto)

    @Query(
        """
    UPDATE cached_messages 
    SET status = :newStatus 
    WHERE chat_id = :chatId
      AND sender_id != :readerId
      AND status != :newStatus
"""
    )
    suspend fun markMessagesAsReadByReader(
        chatId: String,
        readerId: String,
        newStatus: MessageStatus = MessageStatus.READ
    )
    @Query("DELETE FROM cached_messages")
    suspend fun clearAllMessages()
}