package net.thechance.mena.faith.data.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

actual class QuranDatabaseBuilder {
    actual fun getBuilder() = Room.databaseBuilder<QuranDatabase>(
        name = databaseUri.removePrefix(pathPrefix),
    ).setDriver(BundledSQLiteDriver())

    internal actual val pathPrefix: String
        get() = "file://"
}