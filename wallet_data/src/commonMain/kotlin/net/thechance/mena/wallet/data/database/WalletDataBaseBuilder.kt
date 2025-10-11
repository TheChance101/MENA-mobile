
package net.thechance.mena.wallet.data.database
import androidx.room.RoomDatabase


expect class WalletDatabaseBuilder {
    fun getBuilder(context:Any?): RoomDatabase.Builder<WalletDatabase>
}
