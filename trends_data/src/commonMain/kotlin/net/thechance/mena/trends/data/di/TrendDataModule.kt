package net.thechance.mena.trends.data.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [NetworkModule::class])
@ComponentScan("net.thechance.mena.trends.data")
class TrendDataModule