package net.thechance.mena.identity.data.dataSource.local.database

import androidx.room.RoomDatabaseConstructor

actual object AppDatabaseConstructor :
    RoomDatabaseConstructor<IdentityDatabase> {
    actual override fun initialize(): IdentityDatabase {
        error("Room is not supported on desktop yet.")
    }
}