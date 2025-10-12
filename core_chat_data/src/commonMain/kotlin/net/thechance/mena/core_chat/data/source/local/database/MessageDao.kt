package net.thechance.mena.core_chat.data.source.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertMessage(message: MessageLocalDto)

    @Query("SELECT * FROM messages WHERE chat_id = :chatId ORDER BY timestamp ASC")
    suspend fun getMessagesByChat(chatId: String): List<MessageLocalDto>

    @Query("UPDATE messages SET message_status = :status WHERE id = :id")
    suspend fun updateMessageStatus(id: String, status: MessageLocalDto.MessageStatus)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessage(id: String)
}