@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package net.thechance.mena.core_chat.data.source.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters

@Database(entities = [MessageLocalDto::class], version = 1)
@ConstructedBy(ChatDatabaseConstructor::class)
@TypeConverters(MessageStatusConverter::class)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun getMessageDao(): MessageDao
}

@Suppress("KotlinNoActualForExpect")
expect object ChatDatabaseConstructor : RoomDatabaseConstructor<ChatDatabase> {
    override fun initialize(): ChatDatabase
}

const val CHAT_DATABASE_NAME = "chat_database.db"