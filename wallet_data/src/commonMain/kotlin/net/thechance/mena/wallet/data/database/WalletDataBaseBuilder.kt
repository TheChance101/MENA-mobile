package net.thechance.mena.wallet.data.database
import androidx.room.RoomDatabase



expect fun getDatabaseBuilder(context: Any? = null): RoomDatabase.Builder<WalletDatabase>