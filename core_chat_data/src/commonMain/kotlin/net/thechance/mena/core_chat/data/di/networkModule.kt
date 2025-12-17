package net.thechance.mena.core_chat.data.di

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import net.thechance.mena.core_chat.data.messagesender.AudioMessageSender
import net.thechance.mena.core_chat.data.messagesender.AyahMessageSender
import net.thechance.mena.core_chat.data.messagesender.ImageMessageSender
import net.thechance.mena.core_chat.data.messagesender.MessageSenderFactory
import net.thechance.mena.core_chat.data.messagesender.TextMessageSender
import net.thechance.mena.core_chat.data.source.remote.network.HttpClientHolder
import net.thechance.mena.core_chat.data.source.remote.network.HttpClientHolderImp
import net.thechance.mena.core_chat.data.source.remote.network.ImageDownloaderImp
import net.thechance.mena.core_chat.data.source.remote.network.WebSocketManager
import net.thechance.mena.core_chat.data.source.remote.network.WebSocketManagerImpl
import net.thechance.mena.core_chat.data.source.remote.network.createChatCoilClient
import net.thechance.mena.core_chat.data.source.remote.network.createMediaHttpClient
import net.thechance.mena.core_chat.data.source.remote.network.httpClientEngineFactory
import net.thechance.mena.core_chat.domain.service.ImageDownloaderService
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val networkModule = module {
    single(named(CHAT_JSON)) {
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        }
    }

    single(named(MEDIA_CLIENT)) {
        createMediaHttpClient(
            httpClientEngineFactory
        )
    }
    single<WebSocketManager> {
        WebSocketManagerImpl(
            baseUrl = get(named(BASE_URL)),
            clientHolder = get(named(CUSTOM_HTTP_CLIENT))
        )
    }

    single<ImageDownloaderService> { ImageDownloaderImp() }

    single<HttpClientHolder>(named(CUSTOM_HTTP_CLIENT)){
        HttpClientHolderImp(
            get(named(BASE_URL)),
            get(),
            httpClientEngineFactory,
            get(named(CHAT_JSON))
        )
    }

    single(named(IMAGE_MESSAGE_SENDER)) { ImageMessageSender(get(named(CUSTOM_HTTP_CLIENT))) }
    single(named(TEXT_MESSAGE_SENDER)) { TextMessageSender(get(), get(named(CHAT_JSON))) }
    single(named(AUDIO_MESSAGE_SENDER)) { AudioMessageSender(get(named(CUSTOM_HTTP_CLIENT))) }
    single(named(AYAH_MESSAGE_SENDER)) { AyahMessageSender(get(), get(named(CHAT_JSON))) }


    single {
        MessageSenderFactory(
            textMessageSender = get(named(TEXT_MESSAGE_SENDER)),
            imageMessageSender = get(named(IMAGE_MESSAGE_SENDER)),
            audioMessageSender = get(named(AUDIO_MESSAGE_SENDER)),
            ayahMessageSender = get(named(AYAH_MESSAGE_SENDER)),
        )
    }

    single<HttpClient>(named(CHAT_COIL_CLIENT)) {
        createChatCoilClient()
    }
}

private const val BASE_URL = "baseUrl"
const val CUSTOM_HTTP_CLIENT = "customHttpClient"
const val MEDIA_CLIENT = "mediaClient"
const val CHAT_JSON = "chatJson"
const val IMAGE_MESSAGE_SENDER = "image_message_sender"
const val TEXT_MESSAGE_SENDER = "text_message_sender"
const val AUDIO_MESSAGE_SENDER = "audio_message_sender"
const val AYAH_MESSAGE_SENDER = "ayah_message_sender"
const val CHAT_COIL_CLIENT = "chat_coil_client"