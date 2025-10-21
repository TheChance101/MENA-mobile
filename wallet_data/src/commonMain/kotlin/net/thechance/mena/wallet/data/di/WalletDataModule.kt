package net.thechance.mena.wallet.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.wallet.data.database.StatementDao
import net.thechance.mena.wallet.data.database.WalletDatabase
import net.thechance.mena.wallet.data.database.WalletDatabaseBuilder
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("net.thechance.mena.wallet.data")
class WalletDataModule {
    @Single
    fun provideWalletDatabase(builder: WalletDatabaseBuilder): WalletDatabase {
        return builder.getBuilder()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    @Single
    fun provideStatementDao(database: WalletDatabase): StatementDao {
        return database.getStatementDao()
    }
}