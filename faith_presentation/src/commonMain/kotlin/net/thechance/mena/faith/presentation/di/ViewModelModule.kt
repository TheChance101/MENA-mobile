package net.thechance.mena.faith.presentation.di

import net.thechance.mena.faith.presentation.feature.quran.bookmark.BookmarkViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val viewModelModule = module {
    viewModelOf(::BookmarkViewModel)
}
