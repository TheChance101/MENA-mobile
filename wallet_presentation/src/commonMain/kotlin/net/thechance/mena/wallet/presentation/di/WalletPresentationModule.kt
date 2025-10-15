package net.thechance.mena.wallet.presentation.di

import net.thechance.mena.wallet.presentation.utils.DefaultStringProvider
import net.thechance.mena.wallet.presentation.utils.StringProvider
import net.thechance.mena.wallet.presentation.utils.getImageSharer
import net.thechance.mena.wallet.presentation.utils.getPdfHandler
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("net.thechance.mena.wallet.presentation")
class WalletPresentationModule{
    @Single
    fun imageSharerProvider() = getImageSharer()

    @Single
    fun providePdfHandler() = getPdfHandler()

    @Single
    fun provideStringProvider(): StringProvider = DefaultStringProvider()
}