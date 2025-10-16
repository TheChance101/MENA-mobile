package net.thechance.mena.core_chat.presentation.screen.home

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class HomeScreenState(
    val isLoading: Boolean = false,
    val isSynced: Boolean = false,
    val isError: Boolean = false,
    val balance: Double = 0.0,
    val chats: List<ChatUiState> = fakeData
) {
    data class ChatUiState @OptIn(ExperimentalUuidApi::class) constructor(
        val id: Uuid,
        val name: String,
        val imageUrl: String?,
        val lastMessage: MessageUiState,
        val status: Status,
    ) {
        data class MessageUiState(
            val text: String,
            val time: String,
            val isMine: Boolean,
        )

        sealed class Status {
            data class UnRead(val count: Int) : Status()
            data object Read : Status()
            data object Sent : Status()
            data object Received : Status()
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
val fakeData = listOf(
    HomeScreenState.ChatUiState(
        id = Uuid.random(),
        name = "Sara Mahmoud",
        imageUrl = "https://i.ibb.co/3y0TLrqV/f685ab4ed41c4c97bc4ffc6b3795175d4a29cd41.jpg",
        lastMessage = HomeScreenState.ChatUiState.MessageUiState(
            time = "12:33 PM",
            isMine = false,
            text = "Hi, how are you?",
        ),
        status = HomeScreenState.ChatUiState.Status.UnRead(2),
    ), HomeScreenState.ChatUiState(
        id = Uuid.random(),
        name = "Maryam Saleh",
        imageUrl = "https://i.ibb.co/DjJLNHm/ef7bf477a8366d411f62a575dc169f0858ca1fec.jpg",
        lastMessage = HomeScreenState.ChatUiState.MessageUiState(
            time = "12:33 PM",
            isMine = false,
            text = "Hi, how are you?",
        ),
        status = HomeScreenState.ChatUiState.Status.UnRead(2),
    ), HomeScreenState.ChatUiState(
        id = Uuid.random(),
        name = "Ayad Saddon",
        imageUrl = null,
        lastMessage = HomeScreenState.ChatUiState.MessageUiState(
            time = "12:33 PM",
            isMine = false,
            text = "Hi, how are you?",
        ),
        status = HomeScreenState.ChatUiState.Status.UnRead(3),
    ), HomeScreenState.ChatUiState(
        id = Uuid.random(),
        name = "Aseel Rahman",
        imageUrl = "https://i.ibb.co/k2tY9Pcf/f538e7ed045b525721bd578ffa86d22fb58a9245.jpg",
        lastMessage = HomeScreenState.ChatUiState.MessageUiState(
            time = "12:33 PM",
            isMine = false,
            text = "Hi, how are you?",
        ),
        status = HomeScreenState.ChatUiState.Status.Received,
    ), HomeScreenState.ChatUiState(
        id = Uuid.random(),
        name = "The princess 👑",
        imageUrl = "https://i.ibb.co/XrjSgT95/6a9470e240d86a266e0d9fa666c6fce3e0659d2e.jpg",
        lastMessage = HomeScreenState.ChatUiState.MessageUiState(
            time = "17-03-2025",
            isMine = false,
            text = "Hi, how are you?",
        ),
        status = HomeScreenState.ChatUiState.Status.Received,
    ), HomeScreenState.ChatUiState(
        id = Uuid.random(),
        name = "Mansour Mogyad Ibrahee...",
        imageUrl = "https://i.ibb.co/m5TCtW9w/afe181934c9bdfd994e1a0f1a9e8ecbb935908a9.jpg",
        lastMessage = HomeScreenState.ChatUiState.MessageUiState(
            time = "17-03-2025",
            isMine = false,
            text = "You: Send a photo",
        ),
        status = HomeScreenState.ChatUiState.Status.Sent,
    ), HomeScreenState.ChatUiState(
        id = Uuid.random(),
        name = "Maha Fares",
        imageUrl = "https://i.ibb.co/0RG3LYJB/10a780bcc5534f15472c4f67093c27d7ea30d861.jpg",
        lastMessage = HomeScreenState.ChatUiState.MessageUiState(
            time = "17-03-2025",
            isMine = false,
            text = "Shared a photo",
        ),
        status = HomeScreenState.ChatUiState.Status.Read,
    ), HomeScreenState.ChatUiState(
        id = Uuid.random(),
        name = "Aseel Rahman",
        imageUrl = "https://i.ibb.co/tPYDxg0L/cedbd762f4ea4d209b0187b254769d0b89988d8e.jpg",
        lastMessage = HomeScreenState.ChatUiState.MessageUiState(
            time = "17-03-2025",
            isMine = false,
            text = "★",

            ),
        status = HomeScreenState.ChatUiState.Status.UnRead(12),
    )
)
