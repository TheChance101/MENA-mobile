package net.thechance.mena.admin_panel.di

import com.russhwolf.settings.Settings
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("net.thechance.mena.admin_panel")
class AppModule {
    @Single
    fun provideSettings(): Settings = Settings()
}