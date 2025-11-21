package net.thechance.mena.faith.data.database

import android.content.Context
import androidx.room.Room

actual class QuranDatabaseBuilder(private val context: Context) {
    actual fun getBuilder() = Room.databaseBuilder<QuranDatabase>(
        context = context.applicationContext,
        name = DATABASE_NAME
    ).createFromAsset(databaseUri.removePrefix(pathPrefix))

    internal actual val pathPrefix: String
        get() = "file:///android_asset/"
}