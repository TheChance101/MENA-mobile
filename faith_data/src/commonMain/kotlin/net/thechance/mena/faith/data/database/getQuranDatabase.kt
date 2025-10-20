package net.thechance.mena.faith.data.database

import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

fun getQuranDatabase(
    builder: RoomDatabase.Builder<QuranDatabase>
): QuranDatabase = builder.setQueryCoroutineContext(context = Dispatchers.IO).build()