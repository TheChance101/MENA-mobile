package net.thechance.mena.core_chat.data.source.local.database.chatSyncTime

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChatSyncTimeDao {
    @Query("SELECT last_sync_time FROM chat_sync_times WHERE chat_id = :chatId LIMIT 1")
    suspend fun getLastSyncTime(chatId: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(chatSyncTimeLocalDto: ChatSyncTimeLocalDto)
}
