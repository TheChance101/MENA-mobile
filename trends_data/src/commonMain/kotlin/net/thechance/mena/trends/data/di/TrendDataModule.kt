package net.thechance.mena.trends.data.di

import io.ktor.client.HttpClient
import net.thechance.mena.trends.data.client.NetworkClient
import net.thechance.mena.trends.data.util.VideoFileHandler
import net.thechance.mena.trends.data.util.getPlatformFileReader
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Module
@ComponentScan("net.thechance.mena.trends.data")
class TrendDataModule {

    @Single
    fun provideFileReader(): VideoFileHandler = getPlatformFileReader()

    @Single
    @Named(DEFAULT_CLIENT_NAME)
    fun provideDefaultHttpClient(): HttpClient = NetworkClient().provideDefaultHttpClient()

    @Single
    @Named(UPLOAD_CLIENT_NAME)
    fun provideUploadHttpClient(): HttpClient = NetworkClient().provideUploadHttpClient()

    companion object {
        const val DEFAULT_CLIENT_NAME = "defaultClient"
        const val UPLOAD_CLIENT_NAME = "uploadClient"
    }
}