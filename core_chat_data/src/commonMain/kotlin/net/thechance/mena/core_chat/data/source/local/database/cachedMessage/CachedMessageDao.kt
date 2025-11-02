package net.thechance.mena.core_chat.data.source.local.database.cachedMessage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.thechance.mena.core_chat.domain.entity.MessageStatus

@Dao
interface CachedMessageDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertMessage(message: CachedMessageLocalDto)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAllMessages(message: List<CachedMessageLocalDto>)

    @Query("SELECT * FROM cached_messages WHERE chat_id = :chatId ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    fun getMessagesByChatIdWithOffset(chatId: String, limit: Int, offset: Int): List<CachedMessageLocalDto>

    @Query("UPDATE cached_messages SET status = :status WHERE id = :id")
    suspend fun updateMessageStatus(id: String, status: MessageStatus)

    @Query("DELETE FROM cached_messages WHERE id = :id")
    suspend fun deleteMessage(id: String)
}