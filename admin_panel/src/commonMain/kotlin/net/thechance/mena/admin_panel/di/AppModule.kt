package net.thechance.mena.admin_panel.di

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("net.thechance.mena.admin_panel")
class AppModule {
    @OptIn(ExperimentalSettingsApi::class)
    @Single
    fun provideSettings(): FlowSettings = (Settings() as ObservableSettings).toFlowSettings()
}