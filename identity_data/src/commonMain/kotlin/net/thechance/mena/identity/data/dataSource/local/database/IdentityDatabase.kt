package net.thechance.mena.identity.data.dataSource.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import net.thechance.mena.identity.data.dataSource.local.database.dao.AddressDao
import net.thechance.mena.identity.data.dataSource.local.database.dao.UserDao
import net.thechance.mena.identity.data.dataSource.local.database.model.AddressEntity
import net.thechance.mena.identity.data.dataSource.local.database.model.UserEntity

@Database(entities = [UserEntity::class, AddressEntity::class], version = 2, exportSchema = true)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class IdentityDatabase : RoomDatabase() {
    abstract fun getUserDao(): UserDao
    abstract fun getAddressDao(): AddressDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<IdentityDatabase> {
    override fun initialize(): IdentityDatabase
}