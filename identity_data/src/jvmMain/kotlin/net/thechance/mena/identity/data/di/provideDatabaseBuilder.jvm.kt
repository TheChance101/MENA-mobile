package net.thechance.mena.identity.data.di

import androidx.room.Room
import androidx.room.RoomDatabase.Builder
import net.thechance.mena.identity.data.dataSource.local.database.IdentityDatabase
import org.koin.core.scope.Scope
import java.io.File

actual fun Scope.provideDatabaseBuilder(): Builder<IdentityDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "identity.db")
    return Room.databaseBuilder<IdentityDatabase>(
        name = dbFile.absolutePath,
    )
}