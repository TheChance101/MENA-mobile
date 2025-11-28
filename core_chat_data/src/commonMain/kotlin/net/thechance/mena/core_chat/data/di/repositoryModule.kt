package net.thechance.mena.core_chat.data.di

import net.thechance.mena.core_chat.data.repository.AudioRecordRepositoryImpl
import net.thechance.mena.core_chat.data.repository.ChatRepositoryImpl
import net.thechance.mena.core_chat.data.repository.ContactsRepositoryImpl
import net.thechance.mena.core_chat.data.repository.MessageRepositoryImpl
import net.thechance.mena.core_chat.data.repository.UserRepositoryImpl
import net.thechance.mena.core_chat.data.repository.WeatherDetailsRepositoryImpl
import net.thechance.mena.core_chat.data.utils.audio.AudioRecorder
import net.thechance.mena.core_chat.data.utils.audio.AudioRecorderImpl
import net.thechance.mena.core_chat.domain.repository.AudioRecordRepository
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.ContactsRepository
import net.thechance.mena.core_chat.domain.repository.MessageRepository
import net.thechance.mena.core_chat.domain.repository.UserRepository
import net.thechance.mena.core_chat.domain.repository.WeatherRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
internal val repositoryModule = module {
    single<ContactsRepository> {
        ContactsRepositoryImpl(
            clientHolder = get(named(CUSTOM_HTTP_CLIENT)),
            contactsProvider = get(),
            dataStore = get()
        )
    }

    single<ChatRepository> {
        ChatRepositoryImpl(
            clientHolder = get(named(CUSTOM_HTTP_CLIENT)),
            webSocketManager = get(),
            cachedChatSummaryDao = get(),
            dataStore = get(),
            authRepository = get(),
            cachedChatDao = get(),
        )
    }

    single<MessageRepository> {
        MessageRepositoryImpl(
            clientHolder = get(named(CUSTOM_HTTP_CLIENT)),
            webSocketManager = get(),
            pendingMessageDao = get(),
            messageSenderFactory = get(),
            json = get(named(CHAT_JSON)),
            cachedMessageDao = get(),
            quranService = get(),
            authRepository = get(),
            chatSyncTimeDao = get()
        )
    }

    single<UserRepository> {
        UserRepositoryImpl(
            clientHolder = get(named(CUSTOM_HTTP_CLIENT)),
        )
    }

    single<AudioRecordRepository> {
        AudioRecordRepositoryImpl(
            client = get(named(MEDIA_CLIENT)),
            fileManager = get(),
            audioRecorder = get(),
        )
    }
    single<AudioRecorder> { AudioRecorderImpl() }

    single<WeatherRepository> {
        WeatherDetailsRepositoryImpl(
            clientHolder = get(named(CUSTOM_HTTP_CLIENT)),
            weatherDao = get()
        )
    }
}
