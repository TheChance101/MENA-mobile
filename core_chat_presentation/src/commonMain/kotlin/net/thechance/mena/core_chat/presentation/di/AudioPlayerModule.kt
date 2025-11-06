package net.thechance.mena.core_chat.presentation.di

import net.thechance.mena.core_chat.presentation.utils.AudioPlayer
import net.thechance.mena.core_chat.presentation.utils.createAudioPlayer
import org.koin.dsl.module

internal val audioPlayerModule = module {
    factory<AudioPlayer> { createAudioPlayer() }
}