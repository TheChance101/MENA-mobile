package net.thechance.mena.core_chat.data.source.local.database.chatSyncTime

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChatSyncTimeDao {
    @Query("SELECT lastSyncTime FROM chat_sync_times WHERE chatId = :chatId LIMIT 1")
    suspend fun getLastSyncTime(chatId: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(chatSyncTime: ChatSyncTime)

    @Query("DELETE FROM chat_sync_times")
    suspend fun clearAllSyncTimes()
}
