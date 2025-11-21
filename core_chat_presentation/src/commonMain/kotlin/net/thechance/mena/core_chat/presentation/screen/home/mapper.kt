package net.thechance.mena.core_chat.presentation.screen.home

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.asr
import mena.core_chat_presentation.generated.resources.dhuhr
import mena.core_chat_presentation.generated.resources.fajr
import mena.core_chat_presentation.generated.resources.isha
import mena.core_chat_presentation.generated.resources.maghrib
import mena.core_chat_presentation.generated.resources.sunrise
import net.thechance.mena.core_chat.domain.entity.ChatSummary
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState.Status
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState.Status.Read
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState.Status.Received
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState.Status.Sent
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState.Status.UnRead
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.PrayerUiState
import net.thechance.mena.faith.domain.entity.PrayerName
import net.thechance.mena.faith.domain.entity.PrayerTime
import org.jetbrains.compose.resources.StringResource
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun ChatSummary.toUi(): ChatUiState {

    val statusMessages = getStatusMessages(lastMessage, unReadMessagesCount)
    val lastMessage = lastMessage?.let {
        ChatUiState.MessageUiState(
            text = it.content,
            isMine = it.isMine,
            time = it.sendAt,
        )
    }
    return ChatUiState(
        id = id,
        name = name,
        imageUrl = imageUrl,
        lastMessage = lastMessage,
        status = statusMessages
    )
}

private fun getStatusMessages(lastMessage: ChatSummary.Message?, unReadMessagesCount: Int): Status {
    if (lastMessage == null) return Received
    return when {
        lastMessage.isMine -> {
            if (unReadMessagesCount == 0) {
                Read
            } else {
                Sent
            }
        }

        else -> {
            if (unReadMessagesCount > 0) {
                UnRead(unReadMessagesCount)
            } else {
                Received
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
fun PrayerTime.toUi(): PrayerUiState = PrayerUiState(
    displayName = getPrayerDisplayNameResource(prayerName = this.name),
    time = this.time.toLocalDateTime(TimeZone.currentSystemDefault()),
)

fun getPrayerDisplayNameResource(prayerName: PrayerName): StringResource = when (prayerName) {
    PrayerName.FAJR -> Res.string.fajr
    PrayerName.DHUHR -> Res.string.dhuhr
    PrayerName.ASR -> Res.string.asr
    PrayerName.MAGHRIB -> Res.string.maghrib
    PrayerName.ISHA -> Res.string.isha
    PrayerName.SUNRISE -> Res.string.sunrise
}