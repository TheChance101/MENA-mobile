package net.thechance.mena.wallet.data.di

import net.thechance.mena.wallet.data.database.StatementDao
import net.thechance.mena.wallet.data.database.WalletDatabase
import net.thechance.mena.wallet.data.database.WalletDatabaseBuilder
import net.thechance.mena.wallet.data.database.getWalletDataBase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("net.thechance.mena.wallet.data")
class WalletDataModule{
    @Single
    fun provideWalletDatabaseBuilder(): WalletDatabaseBuilder {
        return WalletDatabaseBuilder()
    }
    @Single
    fun provideWalletDatabase(builder: WalletDatabaseBuilder): WalletDatabase {
        return getWalletDataBase(builder.getBuilder())
    }

    @Single
    fun provideStatementDao(database: WalletDatabase): StatementDao {
        return database.getStatementDao()
    }
}