package net.thechance.mena.identity.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import net.thechance.mena.identity.data.dataSource.local.database.IdentityDatabase
import org.koin.core.scope.Scope

actual fun Scope.provideDatabaseBuilder(): RoomDatabase.Builder<IdentityDatabase> {
    val appContext = this.get<Context>().applicationContext
    val dbFile = appContext.getDatabasePath("identity.db")
    return Room.databaseBuilder<IdentityDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}