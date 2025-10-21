package net.thechance.mena.wallet.data.database

import androidx.room.RoomDatabase
import org.koin.core.annotation.Single

@Single
expect class WalletDatabaseBuilder() {
    fun getBuilder(): RoomDatabase.Builder<WalletDatabase>
}
