package net.thechance.mena.trends.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.trends.data.local.database.TrendsDatabase
import net.thechance.mena.trends.data.local.database.TrendsDatabaseBuilder
import net.thechance.mena.trends.data.local.database.UserEngagementDao
import net.thechance.mena.trends.data.remote.client.NetworkClient
import net.thechance.mena.trends.data.util.VideoFileHandler
import net.thechance.mena.trends.data.util.getPlatformFileReader
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

const val DEFAULT_CLIENT_NAME = "defaultClient"
const val UPLOAD_CLIENT_NAME = "uploadClient"

@Module
@ComponentScan("net.thechance.mena.trends.data")
class TrendDataModule {

    @Single
    fun provideFileReader(): VideoFileHandler = getPlatformFileReader()

    @Single
    fun provideDatabase(builder: TrendsDatabaseBuilder): TrendsDatabase {
        return builder.getBuilder()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    @Single
    fun provideUserEngagementDao(database: TrendsDatabase): UserEngagementDao {
        return database.userEngagementDao()
    }

    @Single
    @Named(DEFAULT_CLIENT_NAME)
    fun provideDefaultHttpClient(): HttpClient = NetworkClient().provideDefaultHttpClient()

    @Single
    @Named(UPLOAD_CLIENT_NAME)
    fun provideUploadHttpClient(): HttpClient = NetworkClient().provideUploadHttpClient()
}