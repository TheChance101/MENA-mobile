package di

import net.thechance.mena.faith.data.repository.QuranRepositoryImpl
import net.thechance.mena.faith.domain.repository.QuranRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val repositoryModule = module {
    singleOf(::QuranRepositoryImpl).bind<QuranRepository>()
}