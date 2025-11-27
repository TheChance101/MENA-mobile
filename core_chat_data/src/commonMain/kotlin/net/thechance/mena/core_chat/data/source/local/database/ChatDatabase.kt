@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package net.thechance.mena.core_chat.data.source.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import net.thechance.mena.core_chat.data.source.local.database.cachedChat.CachedChatDao
import net.thechance.mena.core_chat.data.source.local.database.cachedChat.CachedChatLocalDto
import net.thechance.mena.core_chat.data.source.local.database.cachedMessage.CachedMessageDao
import net.thechance.mena.core_chat.data.source.local.database.cachedMessage.CachedMessageLocalDto
import net.thechance.mena.core_chat.data.source.local.database.chatSyncTime.ChatSyncTime
import net.thechance.mena.core_chat.data.source.local.database.chatSyncTime.ChatSyncTimeDao
import net.thechance.mena.core_chat.data.source.local.database.pendingMessage.PendingMessageDao
import net.thechance.mena.core_chat.data.source.local.database.pendingMessage.PendingMessageLocalDto
import net.thechance.mena.core_chat.data.source.local.database.cachedChatSummary.CachedChatSummaryDao
import net.thechance.mena.core_chat.data.source.local.database.cachedChatSummary.CachedChatSummaryDto
import net.thechance.mena.core_chat.data.source.local.database.cachedWeather.CachedWeatherDao
import net.thechance.mena.core_chat.data.source.local.database.cachedWeather.CachedWeatherLocalDto

@Database(
    entities = [PendingMessageLocalDto::class, CachedMessageLocalDto::class, CachedChatLocalDto::class, ChatSyncTime::class, CachedChatSummaryDto::class, CachedWeatherLocalDto::class],
    version = 1
)
@ConstructedBy(ChatDatabaseConstructor::class)
@TypeConverters(MessageConverter::class)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun getChatSummaryDao(): CachedChatSummaryDao
    abstract fun getPendingMessageDao(): PendingMessageDao
    abstract fun getCachedMessageDao(): CachedMessageDao
    abstract fun getChatDao(): CachedChatDao
    abstract fun getChatSyncTimeDao(): ChatSyncTimeDao
    abstract fun getWeatherDao(): CachedWeatherDao
}

@Suppress("KotlinNoActualForExpect")
expect object ChatDatabaseConstructor : RoomDatabaseConstructor<ChatDatabase> {
    override fun initialize(): ChatDatabase
}

const val CHAT_DATABASE_NAME = "chat_database.db"