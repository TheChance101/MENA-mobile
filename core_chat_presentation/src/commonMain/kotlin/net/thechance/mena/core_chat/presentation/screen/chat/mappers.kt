@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.today
import mena.core_chat_presentation.generated.resources.yesterday
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageContent.Audio
import net.thechance.mena.core_chat.domain.entity.MessageContent.Ayah
import net.thechance.mena.core_chat.domain.entity.MessageContent.Image
import net.thechance.mena.core_chat.domain.entity.MessageContent.Money
import net.thechance.mena.core_chat.domain.entity.MessageContent.Text
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.presentation.utils.UiText
import net.thechance.mena.core_chat.presentation.utils.format
import net.thechance.mena.core_chat.presentation.utils.minusDays
import net.thechance.mena.core_chat.presentation.utils.now
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


fun LocalDate.toLabel(
    today: LocalDate,
    yesterday: LocalDate,
    todayLabel: UiText = UiText.StringRes(Res.string.today),
    yesterdayLabel: UiText = UiText.StringRes(Res.string.yesterday)
): UiText = when (this) {
    today -> todayLabel
    yesterday -> yesterdayLabel
    else -> UiText.DynamicString(format())
}

fun List<MessageUiState>.addDateSeparators(): List<ChatListItem> {
    val today = LocalDateTime.now().date
    val yesterday = today.minusDays(1)
    return groupBy { it.messageDetails.sendTime.date }
        .flatMap { (date, messages) ->
            val markedMessages = messages.markIsLastMessages()
            buildList {
                addAll(markedMessages)
                add(DateSeparator(date.toLabel(today, yesterday)))
            }
        }
}


fun List<MessageUiState>.markIsLastMessages(): List<MessageUiState> {
    if (isEmpty()) return this
    var lastIsMine = first().messageDetails.isMine
    return mapIndexed { index, messageUiState ->
        val isLastInSeries = index == 0 || messageUiState.messageDetails.isMine != lastIsMine
        lastIsMine = messageUiState.messageDetails.isMine
        messageUiState.copyMessage(
            messageUiState.messageDetails.copy(
                isLastInSeries = isLastInSeries
            )
        )
    }
}

fun List<ChatListItem>.groupImages(keepSeparatedAfter: LocalDateTime): List<ChatListItem> {
    val grouped = mutableListOf<ChatListItem>()
    var tempImagesGroup = ImagesGroupChatItem(imagesUiState = emptyList())
    for (item in this) {
        if (item !is ImageMessageUiState) {
            if (tempImagesGroup.imagesUiState.isNotEmpty()) {
                grouped.add(tempImagesGroup)
                tempImagesGroup = ImagesGroupChatItem(imagesUiState = emptyList())
            }
            grouped.add(item)
            continue
        }

        if (item.messageDetails.status == MessageStatus.FAILED) {
            if (tempImagesGroup.imagesUiState.isNotEmpty()) {
                grouped.add(tempImagesGroup)
                tempImagesGroup = ImagesGroupChatItem(imagesUiState = emptyList())
            }
            grouped.add(item)
            continue
        }

        if (
            tempImagesGroup.imagesUiState.isNotEmpty()
            && tempImagesGroup.imagesUiState.last().messageDetails.isMine != item.messageDetails.isMine
        ) {
            grouped.add(tempImagesGroup)
            tempImagesGroup = ImagesGroupChatItem(imagesUiState = listOf(item))
            continue
        }

        if (item.messageDetails.sendTime >= keepSeparatedAfter) {
            if (tempImagesGroup.imagesUiState.isNotEmpty()) {
                grouped.add(tempImagesGroup)
                tempImagesGroup = ImagesGroupChatItem(imagesUiState = emptyList())
            }
            grouped.add(item)
            continue
        }

        tempImagesGroup = tempImagesGroup.copy(imagesUiState = tempImagesGroup.imagesUiState + item)
    }
    if (tempImagesGroup.imagesUiState.isNotEmpty()) {
        grouped.add(tempImagesGroup)
    }
    return grouped
}

fun generateWaveformData(): List<Float> {
    return List(50) { Random.nextFloat() * 0.8f + 0.2f }
}

fun List<ChatListItem>.toggleMessageInfo(messageId: Uuid): List<ChatListItem> = map { item ->
    when {
        item is TextMessageUiState && item.messageDetails.id == messageId ->
            item.copy(messageDetails = item.messageDetails.copy(isVisibleMessageInfo = !item.messageDetails.isVisibleMessageInfo))

        item is AudioMessageUiState && item.messageDetails.id == messageId ->
            item.copy(messageDetails = item.messageDetails.copy(isVisibleMessageInfo = !item.messageDetails.isVisibleMessageInfo))

        item is AyahMessageUiState && item.messageDetails.id == messageId ->
            item.copy(messageDetails = item.messageDetails.copy(isVisibleMessageInfo = !item.messageDetails.isVisibleMessageInfo))

        else -> item
    }
}

fun Message.toUi(): MessageUiState {
    val messageDetails = MessageDetailsUiState(
        id = id,
        senderId = senderId,
        chatId = chatId,
        sendTime = sendAt,
        status = status,
        isMine = isMine,
        reactions = reactions,
    )
    return when (val content = content) {
        is Image -> ImageMessageUiState(
            imageDate = content.data,
            messageDetails = messageDetails
        )

        is Audio -> AudioMessageUiState(
            data = content.data,
            isPlaying = false,
            isLoading = false,
            progress = 0f,
            duration = content.audioDurationMs ?: 0,
            waveformData = generateWaveformData(),
            messageDetails = messageDetails
        )

        is Text -> TextMessageUiState(
            text = content.text,
            messageDetails = messageDetails
        )

        is Ayah -> AyahMessageUiState(
            surahId = content.surahId,
            ayahContent = content.ayahContent,
            ayahNumber = content.ayahNumber,
            surahName = content.surahName,
            messageDetails = messageDetails
        )

        is Money -> MoneyMessageUiState(
            amount = content.amount,
            messageDetails = messageDetails
        )
    }
}

fun MessageUiState.toEntity(): Message {
    return when (this) {
        is AudioMessageUiState -> Message(
            chatId = messageDetails.chatId,
            senderId = messageDetails.senderId,
            content = Audio(data = data, audioDurationMs = duration),
            id = messageDetails.id,
            sendAt = messageDetails.sendTime,
            status = messageDetails.status,
            isMine = messageDetails.isMine,
            reactions = messageDetails.reactions
        )

        is ImageMessageUiState -> {
            Message(
                chatId = messageDetails.chatId,
                senderId = messageDetails.senderId,
                content = Image(data = imageDate),
                id = messageDetails.id,
                sendAt = messageDetails.sendTime,
                status = messageDetails.status,
                isMine = messageDetails.isMine,
                reactions = messageDetails.reactions,
            )
        }

        is TextMessageUiState -> {
            Message(
                chatId = messageDetails.chatId,
                senderId = messageDetails.senderId,
                content = Text(text = text),
                id = messageDetails.id,
                sendAt = messageDetails.sendTime,
                status = messageDetails.status,
                isMine = messageDetails.isMine,
                reactions = messageDetails.reactions,
            )
        }

        is AyahMessageUiState -> {
            Message(
                chatId = messageDetails.chatId,
                senderId = messageDetails.senderId,
                content = Ayah(
                    surahId = surahId,
                    surahName = surahName,
                    ayahContent = ayahContent,
                    ayahNumber = ayahNumber
                ),
                id = messageDetails.id,
                sendAt = messageDetails.sendTime,
                status = messageDetails.status,
                isMine = messageDetails.isMine,
                reactions = messageDetails.reactions,
            )
        }

        is MoneyMessageUiState -> {
            Message(
                chatId = messageDetails.chatId,
                senderId = messageDetails.senderId,
                content = Money(amount = amount),
                id = messageDetails.id,
                sendAt = messageDetails.sendTime,
                status = messageDetails.status,
                isMine = messageDetails.isMine,
                reactions = messageDetails.reactions,
            )
        }
    }
}