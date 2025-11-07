package net.thechance.mena.core_chat.data.di

import net.thechance.mena.core_chat.data.utils.FileManager
import net.thechance.mena.core_chat.data.utils.createFileManager
import org.koin.dsl.module

val fileManagerModule = module {
    single<FileManager> {
        createFileManager()
    }
}