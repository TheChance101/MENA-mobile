package net.thechance.mena.core_chat.data.local_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.thechance.mena.core_chat.data.local_database.dto.MessageLocalDto

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageLocalDto)

    @Query("SELECT * FROM MessageLocalDto WHERE chat_id = :chatId ORDER BY timestamp ASC")
    suspend fun getMessagesByChat(chatId: String): List<MessageLocalDto>

    @Query("UPDATE MessageLocalDto SET message_status = :status WHERE id = :id")
    suspend fun updateMessageStatus(id: String, status: MessageLocalDto.MessageStatus)

    @Query("DELETE FROM MessageLocalDto WHERE id = :id")
    suspend fun deleteMessage(id: String)
}