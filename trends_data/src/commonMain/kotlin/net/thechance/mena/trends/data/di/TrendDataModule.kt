package net.thechance.mena.trends.data.di

import net.thechance.mena.trends.data.util.FileReader
import net.thechance.mena.trends.data.util.getPlatformFileReader
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("net.thechance.mena.trends.data")
class TrendDataModule {

    @Single
    fun provideFileReader(): FileReader = getPlatformFileReader()
}