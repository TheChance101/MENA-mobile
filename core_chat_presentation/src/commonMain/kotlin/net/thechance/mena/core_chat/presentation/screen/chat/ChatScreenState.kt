package net.thechance.mena.core_chat.presentation.screen.chat

import kotlinx.datetime.LocalDateTime
import net.thechance.mena.core_chat.presentation.utils.minusMinutes
import net.thechance.mena.core_chat.presentation.utils.now

data class ChatScreenState(
    val chat: ChatUiState = dummyChat,
    val chatListItems: List<ChatListItem> = dummyMessages.markLastInSeries().withDateSeparators(),
    val inputMessage: String = ""
)

//dummies
val dummyMessages = listOf(
    // 🟢 Newest - Sending
    TextMessageUiState(
        id = "20",
        senderId = "noor",
        sendTime = LocalDateTime.now().minusMinutes(2),
        status = MessageStatus.SENDING,
        isMine = true,
        text = "Uploading now… please wait a sec ⏳"
    ),
    TextMessageUiState(
        id = "19",
        senderId = "bilal",
        sendTime = LocalDateTime.now().minusMinutes(5),
        status = MessageStatus.READ,
        isMine = false,
        text = "Argh, my internet dropped right when I was sending the file. Typical! 😤"
    ),
    TextMessageUiState(
        id = "18",
        senderId = "noor",
        sendTime = LocalDateTime.now().minusMinutes(7),
        status = MessageStatus.FAILED,
        isMine = true,
        text = "Haha, no worries. Happens to me all the time."
    ),
    TextMessageUiState(
        id = "17",
        senderId = "bilal",
        sendTime = LocalDateTime.now().minusMinutes(10),
        status = MessageStatus.SENT,
        isMine = false,
        text = "Quick question—are you free this weekend? I was thinking maybe we could finally test the prototype together."
    ),
    TextMessageUiState(
        id = "16",
        senderId = "noor",
        sendTime = LocalDateTime.now().minusMinutes(12),
        status = MessageStatus.SENT,
        isMine = true,
        text = "Yes! I’d love that. Honestly, I’ve been waiting to see how it looks in action after all these months of development."
    ),
    TextMessageUiState(
        id = "15",
        senderId = "bilal",
        sendTime = LocalDateTime.now().minusMinutes(15),
        status = MessageStatus.READ,
        isMine = false,
        text = "Perfect 👌"
    ),
    TextMessageUiState(
        id = "14",
        senderId = "noor",
        sendTime = LocalDateTime.now().minusMinutes(17),
        status = MessageStatus.READ,
        isMine = true,
        text = "By the way, I remembered something funny from last year. Remember that bug where every single button in the app turned red? 😂 We freaked out thinking the database was corrupted."
    ),
    TextMessageUiState(
        id = "13",
        senderId = "bilal",
        sendTime = LocalDateTime.now().minusMinutes(20),
        status = MessageStatus.READ,
        isMine = false,
        text = "Hahaha oh my god, yes. I still have a screenshot of that somewhere. I called it ‘The Red Wedding’ of our app. 😅"
    ),
    TextMessageUiState(
        id = "12",
        senderId = "noor",
        sendTime = LocalDateTime.now().minusMinutes(22),
        status = MessageStatus.READ,
        isMine = true,
        text = "😂😂 Stop, I’m dying. You really should make a blooper reel of all our funniest dev moments."
    ),
    TextMessageUiState(
        id = "11",
        senderId = "bilal",
        sendTime = LocalDateTime.now().minusMinutes(25),
        status = MessageStatus.READ,
        isMine = false,
        text = "Not a bad idea, actually. Could be a fun lightning talk for the next dev meetup."
    ),
    TextMessageUiState(
        id = "10",
        senderId = "noor",
        sendTime = LocalDateTime.now().minusMinutes(28),
        status = MessageStatus.READ,
        isMine = true,
        text = "Yessss, do it!"
    ),
    TextMessageUiState(
        id = "9",
        senderId = "bilal",
        sendTime = LocalDateTime.now().minusMinutes(30),
        status = MessageStatus.READ,
        isMine = false,
        text = "Anyway, speaking of meetups—did you ever hear back from that recruiter? You mentioned you had an interview lined up."
    ),
    TextMessageUiState(
        id = "8",
        senderId = "noor",
        sendTime = LocalDateTime.now().minusMinutes(33),
        status = MessageStatus.READ,
        isMine = true,
        text = "Oh yeah, that! It went surprisingly well. The questions were tough, but I managed to talk through my thought process instead of panicking. I think they liked that."
    ),
    TextMessageUiState(
        id = "7",
        senderId = "bilal",
        sendTime = LocalDateTime.now().minusMinutes(35),
        status = MessageStatus.READ,
        isMine = false,
        text = "That’s the way to do it 👏 They care more about how you think than just the final answer."
    ),
    TextMessageUiState(
        id = "6",
        senderId = "noor",
        sendTime = LocalDateTime.now().minusMinutes(37),
        status = MessageStatus.READ,
        isMine = true,
        text = "True true. Fingers crossed 🤞"
    ),
    TextMessageUiState(
        id = "5",
        senderId = "bilal",
        sendTime = LocalDateTime.now().minusMinutes(40),
        status = MessageStatus.READ,
        isMine = false,
        text = "You’ll crush it, I know you will."
    ),
    TextMessageUiState(
        id = "4",
        senderId = "noor",
        sendTime = LocalDateTime.now().minusMinutes(43),
        status = MessageStatus.READ,
        isMine = true,
        text = "Awww thanks 😊"
    ),
    TextMessageUiState(
        id = "3",
        senderId = "bilal",
        sendTime = LocalDateTime.now().minusMinutes(45),
        status = MessageStatus.READ,
        isMine = false,
        text = "Just speaking facts 😎"
    ),
    TextMessageUiState(
        id = "2",
        senderId = "noor",
        sendTime = LocalDateTime.now().minusMinutes(47),
        status = MessageStatus.READ,
        isMine = true,
        text = "Haha okay okay, Mr. Confidence."
    ),
    TextMessageUiState(
        id = "1",
        senderId = "bilal",
        sendTime = LocalDateTime.now().minusMinutes(50),
        status = MessageStatus.READ,
        isMine = false,
        text = "What can I say, I’m on a roll today."
    ),

    TextMessageUiState(
        id = "old_b1",
        senderId = "bilal",
        sendTime = LocalDateTime.now().minusMinutes(80),
        status = MessageStatus.READ,
        isMine = false,
        text = "Hey"
    ),
    TextMessageUiState(
        id = "old_b2",
        senderId = "bilal",
        sendTime = LocalDateTime.now().minusMinutes(1350),
        status = MessageStatus.READ,
        isMine = false,
        text = "Are you there?"
    ),
    TextMessageUiState(
        id = "old_b3",
        senderId = "bilal",
        sendTime = LocalDateTime.now().minusMinutes(1400),
        status = MessageStatus.READ,
        isMine = false,
        text = "I just wanted to ask something real quick."
    ),

    TextMessageUiState(
        id = "old_n1",
        senderId = "noor",
        sendTime = LocalDateTime.now().minusMinutes(2800),
        status = MessageStatus.READ,
        isMine = true,
        text = "Hey Bilal 👋 just saw your messages."
    ),
    TextMessageUiState(
        id = "old_n2",
        senderId = "noor",
        sendTime = LocalDateTime.now().minusMinutes(2870),
        status = MessageStatus.READ,
        isMine = true,
        text = "Sorry, I was away from my phone."
    ),
    TextMessageUiState(
        id = "old_n3",
        senderId = "noor",
        sendTime = LocalDateTime.now().minusMinutes(2880),
        status = MessageStatus.READ,
        isMine = true,
        text = "What’s up?"
    )
)


val dummyChat = ChatUiState(
    name = "Bilal Azzam",
    avatarUrl = "https://avatars.githubusercontent.com/u/75501067?v=4"
)
