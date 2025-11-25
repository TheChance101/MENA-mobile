package net.thechance.mena.core_chat.data.source.local.database.cachedChat

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedChatDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertChat(chat: CachedChatLocalDto)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAllChats(chats: List<CachedChatLocalDto>)

    @Query("SELECT * FROM cached_chats WHERE id = :chatId")
    suspend fun getChatById(chatId: String): CachedChatLocalDto?

    @Query("DELETE FROM cached_chats WHERE id = :id")
    suspend fun deleteChatById(id: String)

    @Query("DELETE FROM cached_chats")
    suspend fun clearAllChats()
}