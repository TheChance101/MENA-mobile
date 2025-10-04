package net.thechance.mena.identity.data.di

import androidx.room.RoomDatabase
import net.thechance.mena.identity.data.dataSource.local.database.IdentityDatabase
import org.koin.core.scope.Scope

expect fun Scope.provideDatabaseBuilder(): RoomDatabase.Builder<IdentityDatabase>