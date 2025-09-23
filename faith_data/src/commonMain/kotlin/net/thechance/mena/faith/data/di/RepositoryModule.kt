package net.thechance.mena.faith.data.di

import net.thechance.mena.faith.data.repository.BookmarkRepositoryImpl
import net.thechance.mena.faith.domain.repository.BookmarkRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val repositoryModule = module {
    singleOf(::BookmarkRepositoryImpl) bind BookmarkRepository::class
}
