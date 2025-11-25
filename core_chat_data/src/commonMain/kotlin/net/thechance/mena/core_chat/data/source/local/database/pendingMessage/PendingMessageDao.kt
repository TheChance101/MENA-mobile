package net.thechance.mena.core_chat.data.source.local.database.pendingMessage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.thechance.mena.core_chat.domain.entity.MessageStatus

@Dao
interface PendingMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: PendingMessageLocalDto)

    @Query("SELECT * FROM pending_messages WHERE chat_id = :chatId ORDER BY timestamp ASC")
    fun getMessagesByChatId(chatId: String): Flow<List<PendingMessageLocalDto>>

    @Query("UPDATE pending_messages SET status = :status WHERE id = :id")
    suspend fun updateMessageStatus(id: String, status: MessageStatus)

    @Query("DELETE FROM pending_messages WHERE id = :id")
    suspend fun deleteMessageById(id: String)

    @Query("DELETE FROM pending_messages WHERE id IN (:ids)")
    suspend fun deleteMessagesByIds(ids: List<String>)

    @Query("DELETE FROM pending_messages")
    suspend fun clearAllPendingMessages()
}