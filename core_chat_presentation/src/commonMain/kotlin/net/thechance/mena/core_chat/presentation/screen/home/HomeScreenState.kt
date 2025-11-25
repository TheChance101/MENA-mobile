package net.thechance.mena.core_chat.presentation.screen.home

import kotlinx.datetime.LocalDateTime
import net.thechance.mena.core_chat.presentation.utils.UiText
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class HomeScreenState(
    val isChatsLoading: Boolean = false,
    val isBalanceLoading: Boolean = false,
    val isPrayerTimeLoading: Boolean = false,
    val isWeatherLoading: Boolean = false,
    val balanceAmount: String = "",
    val chats: List<ChatUiState> = emptyList(),
    val prayerUiState: PrayerUiState? = null,
    val weatherUiState: WeatherUiState? = null
) {
    data class ChatUiState @OptIn(ExperimentalUuidApi::class) constructor(
        val id: Uuid,
        val name: String,
        val imageUrl: String?,
        val lastMessage: MessageUiState?,
        val status: Status,
    ) {
        data class MessageUiState(
            val text: UiText,
            val isMine: Boolean,
            val time: LocalDateTime
        )

        sealed class Status {
            data class UnRead(val count: Int) : Status()
            data object Read : Status()
            data object Sent : Status()
            data object Received : Status()
        }
    }

    data class PrayerUiState(
        val displayName: StringResource,
        val time: LocalDateTime,
    )

    data class WeatherUiState(
        val currentTemperature: String,
        val weatherCondition: StringResource?,
        val maxTemperature: String,
        val minTemperature: String,
    )
}
