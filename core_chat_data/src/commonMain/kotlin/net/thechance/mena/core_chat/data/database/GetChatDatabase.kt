package net.thechance.mena.core_chat.data.database

import kotlinx.coroutines.Dispatchers
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.IO

fun getChatDatabase(builder: RoomDatabase.Builder<ChatDatabase>): ChatDatabase = builder
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()